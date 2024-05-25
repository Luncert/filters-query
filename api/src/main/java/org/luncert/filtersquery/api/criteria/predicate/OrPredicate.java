package org.luncert.filtersquery.api.criteria.predicate;

import org.luncert.filtersquery.api.criteria.Predicate;

public class OrPredicate extends BoolPredicate {

  public OrPredicate(Predicate[] predicates) {
    super(predicates);
  }

  @Override
  protected String getOperator() {
    return "or";
  }
}
