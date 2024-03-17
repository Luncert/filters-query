package org.luncert.filtersquery.api.criteria.predicate;

import org.luncert.filtersquery.api.criteria.Predicate;

public class AndPredicate extends BoolPredicate {

  public AndPredicate(Predicate[] predicates) {
    super(predicates);
  }

  @Override
  protected String getOperator() {
    return "and";
  }
}
