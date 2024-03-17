package org.luncert.filtersquery.api.criteria.predicate;

import org.luncert.filtersquery.api.criteria.Reference;
import org.luncert.filtersquery.api.criteria.Value;

public class StartsWithPredicate extends PredicateWithReferenceAndSingleValue {

  public StartsWithPredicate(Reference ref, Value value) {
    super(ref, value);
  }

  @Override
  public String toString() {
    return ref + " startsWith " + value;
  }
}
