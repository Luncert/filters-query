package org.lks.filtersquery.luceneimpl.builder;

import org.apache.lucene.search.Query;

public abstract class BasicTypedQueryBuilder<T> implements TypedQueryBuilder<T> {

  @Override
  public Query empty(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query notEmpty(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query equal(String name, String literalValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query notEqual(String name, String literalValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query greaterEqualThan(String name, String literalValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query greaterThan(String name, String literalValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query lessEqualThan(String name, String literalValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query lessThan(String name, String literalValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query between(String name, String startValue, String endValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query startsWith(String name, String literalValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query endsWith(String name, String literalValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query like(String name, String literalValue) {
    throw new UnsupportedOperationException();
  }
}
