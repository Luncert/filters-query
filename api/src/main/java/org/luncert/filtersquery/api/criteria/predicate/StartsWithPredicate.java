package org.luncert.filtersquery.api.criteria.predicate;

import org.luncert.filtersquery.api.criteria.Reference;
import org.luncert.filtersquery.api.criteria.Value;

public class StartsWithPredicate extends PredicateWithReference {

  private final Value value;

  public StartsWithPredicate(Reference ref, Value value) {
    super(ref);
    this.value = value;
  }

  @Override
  public String toString() {
    return ref + " startsWith " + value;
  }
}
