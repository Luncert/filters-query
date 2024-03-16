package org.luncert.filtersquery.api.criteria.predicate;

import org.luncert.filtersquery.api.criteria.Reference;

public class IsEmptyPredicate extends PredicateWithReference {

  public IsEmptyPredicate(Reference ref) {
    super(ref);
  }

  @Override
  public String toString() {
    return ref + " = empty";
  }
}
