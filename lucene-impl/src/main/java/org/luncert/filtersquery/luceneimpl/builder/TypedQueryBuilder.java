package org.luncert.filtersquery.luceneimpl.builder;

import java.util.List;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;

public interface TypedQueryBuilder<T> {

  Object convertDocFieldToJavaType(IndexableField field);

  SortField.Type getSortType();

  Query equal(String name, String literalValue);

  Query notEqual(String name, String literalValue);

  Query empty(String name);

  Query notEmpty(String name);

  Query in(String name, List<String> literalValues);

  Query greaterEqualThan(String name, String literalValue);

  Query greaterThan(String name, String literalValue);

  Query lessEqualThan(String name, String literalValue);

  Query lessThan(String name, String literalValue);

  Query between(String name, String startValue, String endValue);

  Query startsWith(String name, String literalValue);

  Query endsWith(String name, String literalValue);

  Query like(String name, String literalValue);
}
