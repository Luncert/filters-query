package org.luncert.filtersquery.api.criteria.predicate;

import org.luncert.filtersquery.api.criteria.Reference;
import org.luncert.filtersquery.api.criteria.Value;

public class BetweenPredicate extends PredicateWithReference {

  private final Value value1;
  private final Value value2;

  public BetweenPredicate(Reference ref, Value value1, Value value2) {
    super(ref);
    this.value1 = value1;
    this.value2 = value2;
  }

  @Override
  public String toString() {
    return ref.name() + " between (" + value1 + ", " + value2 + ")";
  }
}
