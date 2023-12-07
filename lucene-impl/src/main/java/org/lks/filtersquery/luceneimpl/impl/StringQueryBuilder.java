package org.lks.filtersquery.luceneimpl.impl;

import static org.apache.lucene.search.BooleanClause.Occur.MUST_NOT;
import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;

public class StringQueryBuilder extends BasicTypedQueryBuilder<String> {

  @Override
  public SortField.Type getSortType() {
    return SortField.Type.STRING;
  }

  @Override
  public Query equal(String name, String literalValue) {
    return new TermQuery(new Term(name, literalValue));
  }

  @Override
  public Query notEqual(String name, String literalValue) {
    return new BooleanQuery.Builder()
        .add(TermRangeQuery.newStringRange(name, "*", "*", true, true), SHOULD)
        .add(new TermQuery(new Term(name, literalValue)), MUST_NOT)
        .build();
  }

  @Override
  public Query greaterEqualThan(String name, String literalValue) {
    return TermRangeQuery.newStringRange(name, literalValue, "*", true, true);
  }

  @Override
  public Query greaterThan(String name, String literalValue) {
    return TermRangeQuery.newStringRange(name, literalValue, "*", false, true);
  }

  @Override
  public Query lessEqualThan(String name, String literalValue) {
    return TermRangeQuery.newStringRange(name, "*", literalValue, true, true);
  }

  @Override
  public Query lessThan(String name, String literalValue) {
    return TermRangeQuery.newStringRange(name, "*", literalValue, true, false);
  }

  @Override
  public Query between(String name, String startValue, String endValue) {
    return TermRangeQuery.newStringRange(name, startValue, endValue, true, true);
  }

  @Override
  public Query startsWith(String name, String literalValue) {
    return super.startsWith(name, literalValue);
  }

  @Override
  public Query endsWith(String name, String literalValue) {
    return super.endsWith(name, literalValue);
  }

  @Override
  public Query like(String name, String literalValue) {
    return super.like(name, literalValue);
  }
}
