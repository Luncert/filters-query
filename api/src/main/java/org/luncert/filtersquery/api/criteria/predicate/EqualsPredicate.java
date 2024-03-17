package org.luncert.filtersquery.api.criteria.predicate;

import org.luncert.filtersquery.api.criteria.Reference;
import org.luncert.filtersquery.api.criteria.Value;

public class EqualsPredicate extends PredicateWithReferenceAndSingleValue {


  public EqualsPredicate(Reference ref, Value value) {
    super(ref, value);
  }

  @Override
  public String toString() {
    return ref + " = " + value;
  }
}
