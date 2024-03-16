package org.luncert.filtersquery.api.criteria.predicate;

import org.luncert.filtersquery.api.criteria.Predicate;
import org.luncert.filtersquery.api.criteria.Reference;

public abstract class PredicateWithReference implements Predicate {

  protected final Reference ref;

  protected PredicateWithReference(Reference ref) {
    this.ref = ref;
  }
}
