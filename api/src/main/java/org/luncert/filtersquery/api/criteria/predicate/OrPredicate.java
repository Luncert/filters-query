package org.luncert.filtersquery.api.criteria.predicate;

import java.util.List;
import org.luncert.filtersquery.api.criteria.Node;
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
