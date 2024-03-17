package org.luncert.filtersquery.api.criteria.dsl;

import org.luncert.filtersquery.api.criteria.Predicate;
import org.luncert.filtersquery.api.criteria.QueryCriteria;
import org.luncert.filtersquery.api.criteria.Sort;
import org.luncert.filtersquery.api.criteria.dsl.spec.IBuildSpec;
import org.luncert.filtersquery.api.criteria.dsl.spec.IFilterBySpec;
import org.luncert.filtersquery.api.criteria.dsl.spec.ILimitSpec;
import org.luncert.filtersquery.api.criteria.dsl.spec.IOffsetSpec;
import org.luncert.filtersquery.api.criteria.dsl.spec.ISortByAndPaginationAndBuildSpec;

/**
 * Help to build query criteria by programing.
 * <pre>
 *  CriteriaBuilder.filterBy(
 *    FQL.and(FQL.between("fieldA", 100, 1000), FQL.like("fieldB", "AMY"))
 *    .sortBy("fieldA", Order.DESC)
 *    .sortBy("fieldB", order.ASC)
 *    .offset()
 *    .limit()
 *    .build()
 * </pre>
 */
public class QueryCriteriaBuilder implements IFilterBySpec,
    ISortByAndPaginationAndBuildSpec, ILimitSpec {

  private Predicate predicate;
  private Sort[] sorts;
  private Integer offset;
  private Integer limit;

  public static QueryCriteriaBuilder create() {
    return new QueryCriteriaBuilder();
  }

  @Override
  public ISortByAndPaginationAndBuildSpec filterBy(Predicate predicate) {
    this.predicate = predicate;
    return this;
  }

  @Override
  public IOffsetSpec sortBy(Sort... sorts) {
    this.sorts = sorts;
    return this;
  }

  @Override
  public ILimitSpec offset(int offset) {
    this.offset = offset;
    return this;
  }

  @Override
  public IBuildSpec limit(int limit) {
    this.limit = limit;
    return this;
  }

  @Override
  public QueryCriteria build() {
    return new QueryCriteria(predicate, sorts, offset, limit);
  }
}
