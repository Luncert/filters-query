package org.luncert.filtersquery.api.criteria.predicate;

import org.luncert.filtersquery.api.criteria.Reference;

public class NotEmptyPredicate extends PredicateWithReference {

  public NotEmptyPredicate(Reference ref) {
    super(ref);
  }

  @Override
  public String toString() {
    return ref + " != empty";
  }
}
