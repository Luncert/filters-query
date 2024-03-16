package org.luncert.filtersquery.api.criteria.predicate;

import org.luncert.filtersquery.api.criteria.Predicate;

public class NotPredicate implements Predicate {

  private final Predicate original;

  public NotPredicate(Predicate original) {
    this.original = original;
  }

  @Override
  public String toString() {
    throw new UnsupportedOperationException();
  }
}
