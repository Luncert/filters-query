package org.lks.filtersquery.luceneimpl.impl;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;

public interface TypedQueryBuilder<T> {

  SortField.Type getSortType();

  Query equal(String name, String literalValue);

  Query notEqual(String name, String literalValue);

  Query empty(String name);

  Query notEmpty(String name);

  Query greaterEqualThan(String name, String literalValue);

  Query greaterThan(String name, String literalValue);

  Query lessEqualThan(String name, String literalValue);

  Query lessThan(String name, String literalValue);

  Query between(String name, String startValue, String endValue);

  Query startsWith(String name, String literalValue);

  Query endsWith(String name, String literalValue);

  Query like(String name, String literalValue);
}
