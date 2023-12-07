package org.lks.filtersquery.luceneimpl.impl;

import static org.apache.lucene.search.BooleanClause.Occur.MUST_NOT;
import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;

import org.apache.lucene.document.IntPoint;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;

public class IntQueryBuilder extends BasicTypedQueryBuilder<Integer> {

  @Override
  public SortField.Type getSortType() {
    return SortField.Type.INT;
  }

  @Override
  public Query equal(String name, String literalValue) {
    return IntPoint.newExactQuery(name, Integer.parseInt(literalValue));
  }

  @Override
  public Query notEqual(String name, String literalValue) {
    return new BooleanQuery.Builder()
        .add(IntPoint.newRangeQuery(name, Integer.MIN_VALUE, Integer.MAX_VALUE), SHOULD)
        .add(IntPoint.newExactQuery(name, Integer.parseInt(literalValue)), MUST_NOT)
        .build();
  }

  @Override
  public Query greaterEqualThan(String name, String literalValue) {
    int v = Integer.parseInt(literalValue);
    return IntPoint.newRangeQuery(name, v, Integer.MAX_VALUE);
  }

  @Override
  public Query greaterThan(String name, String literalValue) {
    int v = Integer.parseInt(literalValue);
    return IntPoint.newRangeQuery(name, Math.min(v, v - 1), Integer.MAX_VALUE);
  }

  @Override
  public Query lessEqualThan(String name, String literalValue) {
    int v = Integer.parseInt(literalValue);
    return IntPoint.newRangeQuery(name, Integer.MIN_VALUE, v);
  }

  @Override
  public Query lessThan(String name, String literalValue) {
    int v = Integer.parseInt(literalValue);
    return IntPoint.newRangeQuery(name, Integer.MIN_VALUE, Math.max(v, v + 1));
  }

  @Override
  public Query between(String name, String startValue, String endValue) {
    return IntPoint.newRangeQuery(name, Integer.parseInt(startValue), Integer.parseInt(endValue));
  }
}
