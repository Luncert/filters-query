package org.luncert.filtersquery.api.criteria;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class QueryCriteria {

  private Predicate predicate;
  private Sort[] sorts;
  private Integer offset;
  private Integer limit;

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("filter by ");
    builder.append(predicate);

    if (sorts != null && sorts.length > 0) {
      builder.append(" sort by ");
      var first = sorts[0];
      builder.append(first.name()).append(' ').append(first.order().getIdenticalName());
      for (int i = 1; i < sorts.length; i++) {
        Sort sort = sorts[i];
        builder.append(", ").append(sort.name()).append(' ')
            .append(sort.order().getIdenticalName());
      }
    }

    if (offset != null && limit != null) {
      builder.append(" offset ").append(offset)
          .append(" limit ").append(limit);
    }

    return builder.toString();
  }
}
