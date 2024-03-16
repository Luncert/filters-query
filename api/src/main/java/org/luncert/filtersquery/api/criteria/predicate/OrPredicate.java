package org.luncert.filtersquery.api.criteria.predicate;

import org.luncert.filtersquery.api.criteria.Predicate;

public class OrPredicate implements Predicate {

  private final Predicate[] predicates;

  public OrPredicate(Predicate[] predicates) {
    this.predicates = predicates;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("(");
    builder.append(predicates[0]);
    for (int i = 1; i < predicates.length; i++) {
      Predicate predicate = predicates[i];
      builder.append(" or ").append(predicate);
    }
    return builder.append(")").toString();
  }
}
