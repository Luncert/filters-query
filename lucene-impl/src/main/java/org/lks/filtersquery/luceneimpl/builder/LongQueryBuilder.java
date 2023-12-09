package org.lks.filtersquery.luceneimpl.builder;

import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;

public class LongQueryBuilder extends BasicTypedQueryBuilder<Long> {

  @Override
  public Object convertDocFieldToJavaType(IndexableField field) {
    return field.numericValue().longValue();
  }

  @Override
  public SortField.Type getSortType() {
    return SortField.Type.LONG;
  }

  @Override
  public Query equal(String name, String literalValue) {
    return LongPoint.newExactQuery(name, Long.parseLong(literalValue));
  }

  @Override
  public Query notEqual(String name, String literalValue) {
    return new BooleanQuery.Builder()
        .add(LongPoint.newRangeQuery(name, Long.MIN_VALUE, Long.MAX_VALUE), BooleanClause.Occur.SHOULD)
        .add(LongPoint.newExactQuery(name, Long.parseLong(literalValue)), BooleanClause.Occur.MUST_NOT)
        .build();
  }

  @Override
  public Query greaterEqualThan(String name, String literalValue) {
    long v = Long.parseLong(literalValue);
    return LongPoint.newRangeQuery(name, v, Long.MAX_VALUE);
  }

  @Override
  public Query greaterThan(String name, String literalValue) {
    long v = Long.parseLong(literalValue);
    return LongPoint.newRangeQuery(name, Math.max(v, v + 1), Long.MAX_VALUE);
  }

  @Override
  public Query lessEqualThan(String name, String literalValue) {
    long v = Long.parseLong(literalValue);
    return LongPoint.newRangeQuery(name, Long.MIN_VALUE, v);
  }

  @Override
  public Query lessThan(String name, String literalValue) {
    long v = Long.parseLong(literalValue);
    return LongPoint.newRangeQuery(name, Long.MIN_VALUE, Math.min(v, v - 1));
  }

  @Override
  public Query between(String name, String startValue, String endValue) {
    return LongPoint.newRangeQuery(name, Long.parseLong(startValue), Long.parseLong(endValue));
  }
}
