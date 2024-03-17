package org.luncert.filtersquery.api.criteria;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Sorts {

  private final List<Sort> sorts;

  @Override
  public String toString() {
    if (sorts == null || sorts.isEmpty()) {
      return "";
    }
    StringBuilder builder = new StringBuilder();
    builder.append(" sort by ");
    var first = sorts.get(0);
    builder.append(first.name()).append(' ').append(first.order().getIdenticalName());
    for (int i = 1; i < sorts.size(); i++) {
      Sort sort = sorts.get(i);
      builder.append(", ").append(sort.name()).append(' ')
          .append(sort.order().getIdenticalName());
    }
    return builder.toString();
  }
}
