package org.luncert.filtersquery.api.criteria;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.luncert.filtersquery.api.criteria.predicate.PredicateWithReference;

@AllArgsConstructor
public class QueryCriteria {

  private Predicate predicate;
  private Sorts sorts;
  private Integer offset;
  private Integer limit;

  public List<Predicate> splitPredicate(String fieldName) {
    var out = new ArrayList<Predicate>();
    var remove = splitPredicate(predicate, fieldName, out);
    switch (remove) {
      case REMOVE -> predicate = null;
      case UNWRAP -> predicate = (Predicate) predicate.getChild(0);
    }
    return out;
  }

  protected SplitPredicateResult splitPredicate(Predicate predicate, String fieldName, List<Predicate> out) {
    if (predicate.isBoolExpression()) {
      int i = 0;
      while (i < predicate.getChildenSize()) {
        var child = predicate.getChild(i);
        var remove = splitPredicate(child.as(Predicate.class), fieldName, out);
        switch (remove) {
          case REMOVE -> predicate.removeChild(i);
          case UNWRAP -> {
            var wrapped = child.getChild(0);
            predicate.replaceChild(i, wrapped);
          }
          case NONE -> i++;
        }
      }
      if (predicate.getChildenSize() == 0) {
        return SplitPredicateResult.REMOVE;
      } else if (predicate.getChildenSize() == 1) {
        return SplitPredicateResult.UNWRAP;
      }
    } else {
      var p = predicate.as(PredicateWithReference.class);
      var field = p.getRef();
      if (field.name().equals(fieldName)) {
        out.add(p);
        return SplitPredicateResult.REMOVE;
      }
    }

    return SplitPredicateResult.NONE;
  }

  protected enum SplitPredicateResult {
    NONE,
    REMOVE,
    UNWRAP
  }

  public QueryCriteria modifyPredicate(PredicateModifier modifier) {
    predicate = modifier.modify(predicate);
    return this;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("filter by ");
    builder.append(predicate)
        .append(sorts);

    if (offset != null && limit != null) {
      builder.append(" offset ").append(offset)
          .append(" limit ").append(limit);
    }

    return builder.toString();
  }

  @FunctionalInterface
  public interface PredicateModifier {

    Predicate modify(Predicate predicate);
  }
}
