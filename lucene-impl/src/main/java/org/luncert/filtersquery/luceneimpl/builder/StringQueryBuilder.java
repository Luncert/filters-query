package org.luncert.filtersquery.luceneimpl.builder;

import static org.apache.lucene.search.BooleanClause.Occur.MUST_NOT;
import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;

import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.WildcardQuery;

public class StringQueryBuilder extends BasicTypedQueryBuilder<String> {

  @Override
  public Query empty(String name) {
    return equal(name, "[NULL_VALUE]");
  }

  @Override
  public Query notEmpty(String name) {
    return notEqual(name, "[NULL_VALUE]");
  }

  @Override
  public Object convertDocFieldToJavaType(IndexableField field) {
    return field.stringValue();
  }

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
        .add(new WildcardQuery(new Term(name, "*")), SHOULD)
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
    return new RegexpQuery(new Term(name, literalValue + "@"));
  }

  @Override
  public Query endsWith(String name, String literalValue) {
    return new RegexpQuery(new Term(name, "@" + literalValue));
  }

  @Override
  public Query like(String name, String literalValue) {
    return new RegexpQuery(new Term(name, "@" + literalValue + "@"));
  }
}
