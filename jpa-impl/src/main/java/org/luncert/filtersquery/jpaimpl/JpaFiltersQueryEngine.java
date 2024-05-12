package org.luncert.filtersquery.jpaimpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import org.luncert.filtersquery.api.FiltersQueryEngine;
import org.luncert.filtersquery.api.exception.FiltersQueryException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public abstract class JpaFiltersQueryEngine<E> extends FiltersQueryEngine {

  private final EntityManager em;
  private final Class<E> entityType;

  @SuppressWarnings("unchecked")
  public JpaFiltersQueryEngine(EntityManager em) {
    this.em = em;
    this.entityType = (Class<E>) ((ParameterizedType) getClass()
        .getGenericSuperclass()).getActualTypeArguments()[0];
  }

  public Page<E> searchPages(String criteria) {
    return searchPages(criteria, (cq, cb, entity) -> {});
  }

  public Page<E> searchPages(String criteria, Pageable pageable) {
    return search(criteria,
        (cq, cb, entity) ->
            cq.orderBy(pageable.getSort().stream()
                .map(order ->
                    order.getDirection().equals(Sort.Direction.ASC)
                        ? cb.asc(entity.get(order.getProperty()))
                        : cb.desc(entity.get(order.getProperty())))
                .toList()),
        result -> {
          if (result.getLimit() != null || result.getOffset() != null) {
            throw new FiltersQueryException(
                "limit and offset parameters not supported here");
          }

          result.getQuery().setFirstResult((int) pageable.getOffset());
          result.getQuery().setMaxResults(pageable.getPageSize());

          List<E> resultList = result.getQuery().getResultList().stream()
              .map(tuple -> tuple.get(0, entityType)).toList();
          long count = result.getCountQuery().getResultList().get(0).get(0, Long.class);

          return new PageImpl<>(resultList, pageable, count);
        });
  }

  protected Page<E> searchPages(String criteria,
                                CriteriaQueryBuilder<E> criteriaQueryBuilder) {
    return search(criteria, criteriaQueryBuilder,
        result -> {
          if (result.getLimit() == null || result.getOffset() == null) {
            throw new FiltersQueryException(
                "limit and offset parameters missing on pageable request");
          }

          List<E> resultList = result.getQuery().getResultList().stream()
              .map(tuple -> tuple.get(0, entityType)).toList();
          long count = result.getCountQuery().getResultList().get(0).get(0, Long.class);

          Pageable pageable = PageRequest.of(result.getOffset() / result.getLimit(),
              result.getLimit());
          return new PageImpl<>(resultList, pageable, count);
        });
  }

  public List<E> search(String criteria) {
    return search(criteria, ((cq, cb, entity) -> cq.multiselect(entity)),
        result -> {
          if (result.getLimit() != null || result.getOffset() != null) {
            throw new FiltersQueryException(
                "only pageable request could apply limit and offset parameters");
          }

          return result.getQuery().getResultList().stream()
              .map(tuple -> tuple.get(0, entityType)).toList();
        });
  }

  protected <R> R search(String criteria,
                         CriteriaQueryBuilder<E> criteriaQueryBuilder,
                         ResultMapper<R> resultMapper) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Tuple> cq = cb.createTupleQuery();
    Root<E> entity = cq.from(entityType);
    if (criteriaQueryBuilder != null) {
      criteriaQueryBuilder.process(cq, cb, entity);
    }
    FiltersQueryBuilderJpaImpl.ResultImpl result = buildQuery(criteria,
        new FiltersQueryBuilderJpaImpl<>(em, cb, cq, entity));
    return resultMapper.map(result);
  }

  @FunctionalInterface
  protected interface CriteriaQueryBuilder<E> {

    void process(CriteriaQuery<Tuple> cq, CriteriaBuilder cb, Root<E> entity);
  }

  @FunctionalInterface
  protected interface ResultMapper<R> {

    R map(FiltersQueryBuilderJpaImpl.ResultImpl result);
  }
}
