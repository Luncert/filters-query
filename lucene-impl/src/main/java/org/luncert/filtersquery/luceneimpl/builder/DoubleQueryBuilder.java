package org.luncert.filtersquery.luceneimpl.builder;

import static org.apache.lucene.search.BooleanClause.Occur.MUST_NOT;
import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;

import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;

public class DoubleQueryBuilder extends BasicTypedQueryBuilder<Double> {

  @Override
  public Object convertDocFieldToJavaType(IndexableField field) {
    return field.numericValue().doubleValue();
  }

  @Override
  public SortField.Type getSortType() {
    return SortField.Type.DOUBLE;
  }

  @Override
  public Query equal(String name, String literalValue) {
    return DoublePoint.newExactQuery(name, Double.parseDouble(literalValue));
  }

  @Override
  public Query notEqual(String name, String literalValue) {
    return new BooleanQuery.Builder()
        .add(DoublePoint.newRangeQuery(name, Double.MIN_VALUE, Double.MAX_VALUE), SHOULD)
        .add(DoublePoint.newExactQuery(name, Double.parseDouble(literalValue)), MUST_NOT)
        .build();
  }

  @Override
  public Query greaterEqualThan(String name, String literalValue) {
    double v = Double.parseDouble(literalValue);
    return DoublePoint.newRangeQuery(name, v, Double.MAX_VALUE);
  }

  @Override
  public Query greaterThan(String name, String literalValue) {
    double v = Double.parseDouble(literalValue);
    return DoublePoint.newRangeQuery(name, Math.max(v, v + 1), Double.MAX_VALUE);
  }

  @Override
  public Query lessEqualThan(String name, String literalValue) {
    double v = Double.parseDouble(literalValue);
    return DoublePoint.newRangeQuery(name, Double.MIN_VALUE, v);
  }

  @Override
  public Query lessThan(String name, String literalValue) {
    double v = Double.parseDouble(literalValue);
    return DoublePoint.newRangeQuery(name, Double.MIN_VALUE, Math.min(v, v - 1));
  }

  @Override
  public Query between(String name, String startValue, String endValue) {
    return DoublePoint.newRangeQuery(name, Double.parseDouble(startValue),
        Double.parseDouble(endValue));
  }
}
