package org.lks.filtersquery.luceneimpl.impl;

import static org.apache.lucene.search.BooleanClause.Occur.MUST_NOT;
import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;

import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;

public class FloatQueryBuilder extends BasicTypedQueryBuilder<Float> {

  @Override
  public Object convertDocFieldToJavaType(IndexableField field) {
    return field.numericValue().floatValue();
  }

  @Override
  public SortField.Type getSortType() {
    return SortField.Type.FLOAT;
  }

  @Override
  public Query equal(String name, String literalValue) {
    return FloatPoint.newExactQuery(name, Float.parseFloat(literalValue));
  }

  @Override
  public Query notEqual(String name, String literalValue) {
    return new BooleanQuery.Builder()
        .add(FloatPoint.newRangeQuery(name, Float.MIN_VALUE, Float.MAX_VALUE), SHOULD)
        .add(FloatPoint.newExactQuery(name, Float.parseFloat(literalValue)), MUST_NOT)
        .build();
  }

  @Override
  public Query greaterEqualThan(String name, String literalValue) {
    float v = Float.parseFloat(literalValue);
    return FloatPoint.newRangeQuery(name, v, Float.MAX_VALUE);
  }

  @Override
  public Query greaterThan(String name, String literalValue) {
    float v = Float.parseFloat(literalValue);
    return FloatPoint.newRangeQuery(name, Math.max(v, v + 1), Float.MAX_VALUE);
  }

  @Override
  public Query lessEqualThan(String name, String literalValue) {
    float v = Float.parseFloat(literalValue);
    return FloatPoint.newRangeQuery(name, Float.MIN_VALUE, v);
  }

  @Override
  public Query lessThan(String name, String literalValue) {
    float v = Float.parseFloat(literalValue);
    return FloatPoint.newRangeQuery(name, Float.MIN_VALUE, Math.min(v, v - 1));
  }

  @Override
  public Query between(String name, String startValue, String endValue) {
    return FloatPoint.newRangeQuery(name, Float.parseFloat(startValue), Float.parseFloat(endValue));
  }
}
