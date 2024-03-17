package org.luncert.filtersquery.api.criteria.predicate;

import org.luncert.filtersquery.api.criteria.Reference;
import org.luncert.filtersquery.api.criteria.Value;

public class LessThanEqualsPredicate extends PredicateWithReferenceAndSingleValue {

  public LessThanEqualsPredicate(Reference ref, Value value) {
    super(ref, value);
  }

  @Override
  public String toString() {
    return ref + " <= " + value;
  }
}
