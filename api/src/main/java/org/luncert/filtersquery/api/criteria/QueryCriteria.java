package org.luncert.filtersquery.api.criteria;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class QueryCriteria {

  private Predicate predicate;
  private Sorts sorts;
  private Integer offset;
  private Integer limit;

  public QueryCriteria modifyPredicate(PredicateModifier modifier) {
    predicate = modifier.modify(predicate);
    return this;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("filter by ");
    builder.append(predicate)
        .append(sorts);

    if (offset != null && limit != null) {
      builder.append(" offset ").append(offset)
          .append(" limit ").append(limit);
    }

    return builder.toString();
  }

  @FunctionalInterface
  public interface PredicateModifier {

    Predicate modify(Predicate predicate);
  }

  @AllArgsConstructor
  class PredicateWrapper {

    private Predicate predicate;
  }
}
