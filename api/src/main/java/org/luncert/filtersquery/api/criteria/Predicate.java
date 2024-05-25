package org.luncert.filtersquery.api.criteria;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.luncert.filtersquery.api.criteria.predicate.AndPredicate;
import org.luncert.filtersquery.api.criteria.predicate.OrPredicate;

public interface Predicate extends Node {

  default boolean isBoolExpression() {
    return false;
  }

  static Predicate and(Predicate... predicates) {
    var list = Arrays.stream(predicates).filter(Objects::nonNull).toList();
    if (list.isEmpty()) {
      throw new IllegalArgumentException("expect at least 1 predicates");
    }
    return list.size() == 1 ? list.get(0)
        : new AndPredicate(list.toArray(new Predicate[0]));
  }

  static Predicate and(List<Predicate> predicates) {
    predicates = predicates.stream().filter(Objects::nonNull).toList();
    if (predicates.isEmpty()) {
      throw new IllegalArgumentException("expect at least 1 predicates");
    }
    return predicates.size() == 1 ? predicates.get(0)
        : new AndPredicate(predicates.toArray(new Predicate[0]));
  }

  static Predicate or(Predicate... predicates) {
    var list = Arrays.stream(predicates).filter(Objects::nonNull).toList();
    if (list.isEmpty()) {
      throw new IllegalArgumentException("expect at least 1 predicates");
    }
    return list.size() == 1 ? list.get(0)
        : new OrPredicate(list.toArray(new Predicate[0]));
  }

  static Predicate or(List<Predicate> predicates) {
    predicates = predicates.stream().filter(Objects::nonNull).toList();
    if (predicates.isEmpty()) {
      throw new IllegalArgumentException("expect at least 1 predicates");
    }
    return predicates.size() == 1 ? predicates.get(0)
        : new OrPredicate(predicates.toArray(new Predicate[0]));
  }

  // static Predicate not(Predicate predicate) {
  //   return new NotPredicate(predicate);
  // }
}
