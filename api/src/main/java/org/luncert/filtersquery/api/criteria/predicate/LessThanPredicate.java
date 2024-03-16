package org.luncert.filtersquery.api.criteria.predicate;

import org.luncert.filtersquery.api.criteria.Reference;
import org.luncert.filtersquery.api.criteria.Value;

public class LessThanPredicate extends PredicateWithReference {

  private final Value value;

  public LessThanPredicate(Reference ref, Value value) {
    super(ref);
    this.value = value;
  }

  @Override
  public String toString() {
    return ref + " < " + value;
  }
}
