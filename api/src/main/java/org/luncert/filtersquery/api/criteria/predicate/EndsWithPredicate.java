package org.luncert.filtersquery.api.criteria.predicate;

import org.luncert.filtersquery.api.criteria.Reference;
import org.luncert.filtersquery.api.criteria.Value;

public class EndsWithPredicate extends PredicateWithReference {

  private final Value value;

  public EndsWithPredicate(Reference ref, Value value) {
    super(ref);
    this.value = value;
  }

  @Override
  public String toString() {
    return ref + " endsWith " + value;
  }
}
