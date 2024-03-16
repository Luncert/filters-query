package org.luncert.filtersquery.api.criteria;

import org.luncert.filtersquery.api.criteria.spec.IBuildSpec;
import org.luncert.filtersquery.api.criteria.spec.IFilterBySpec;
import org.luncert.filtersquery.api.criteria.spec.ILimitSpec;
import org.luncert.filtersquery.api.criteria.spec.IOffsetSpec;
import org.luncert.filtersquery.api.criteria.spec.ISortByAndPaginationAndBuildSpec;

/**
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
public class CriteriaBuilder implements IFilterBySpec, ISortByAndPaginationAndBuildSpec, ILimitSpec {

  private Predicate predicate;
  private Sort[] sorts;
  private Integer offset;
  private Integer limit;

  public static CriteriaBuilder create() {
    return new CriteriaBuilder();
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
  public String build() {
    StringBuilder builder = new StringBuilder("filter by ");
    builder.append(predicate);

    if (sorts != null) {
      builder.append(" sort by ");
      var first = sorts[0];
      builder.append(first.name()).append(' ').append(first.order().getIdenticalName());
      for (int i = 1; i < sorts.length; i++) {
        Sort sort = sorts[i];
        builder.append(", ").append(sort.name()).append(' ').append(sort.order().getIdenticalName());
      }
    }

    if (offset != null && limit != null) {
      builder.append(" offset ").append(offset)
          .append(" limit ").append(limit);
    }

    return builder.toString();
  }
}
