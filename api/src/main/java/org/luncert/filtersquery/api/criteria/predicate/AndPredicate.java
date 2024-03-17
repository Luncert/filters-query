package org.luncert.filtersquery.api.criteria.predicate;

import java.util.List;
import org.luncert.filtersquery.api.criteria.Node;
import org.luncert.filtersquery.api.criteria.Predicate;

public class AndPredicate implements Predicate {

  private final Predicate[] predicates;

  public AndPredicate(Predicate[] predicates) {
    this.predicates = predicates;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("(");
    builder.append(predicates[0]);
    for (int i = 1; i < predicates.length; i++) {
      Predicate predicate = predicates[i];
      builder.append(" and ").append(predicate);
    }
    return builder.append(")").toString();
  }

  @Override
  public List<Node> getChildren() {
    return List.of(predicates);
  }
}
