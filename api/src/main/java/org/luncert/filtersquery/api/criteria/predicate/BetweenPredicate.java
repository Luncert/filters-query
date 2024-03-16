package org.luncert.filtersquery.api.criteria.predicate;

import org.luncert.filtersquery.api.criteria.Reference;
import org.luncert.filtersquery.api.criteria.Value;

public class BetweenPredicate extends PredicateWithReference {

  private final Value a;
  private final Value b;

  public BetweenPredicate(Reference ref, Value a, Value b) {
    super(ref);
    this.a = a;
    this.b = b;
  }

  @Override
  public String toString() {
    return ref.name() + " between (" + a + ", " + b + ")";
  }
}
