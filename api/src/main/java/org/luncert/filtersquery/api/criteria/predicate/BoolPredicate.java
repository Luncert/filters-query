package org.luncert.filtersquery.api.criteria.predicate;

import java.util.List;
import lombok.AllArgsConstructor;
import org.luncert.filtersquery.api.criteria.Node;
import org.luncert.filtersquery.api.criteria.Predicate;

@AllArgsConstructor
public abstract class BoolPredicate implements Predicate {

  protected final Predicate[] predicates;

  protected abstract String getOperator();

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("(");
    builder.append(predicates[0]);
    for (int i = 1; i < predicates.length; i++) {
      Predicate predicate = predicates[i];
      builder.append(" ").append(getOperator()).append(" ").append(predicate);
    }
    return builder.append(")").toString();
  }

  @Override
  public List<Node> getChildren() {
    return List.of(predicates);
  }
}
