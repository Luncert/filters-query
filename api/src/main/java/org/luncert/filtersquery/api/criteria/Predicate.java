package org.luncert.filtersquery.api.criteria;

import java.util.List;
import org.luncert.filtersquery.api.criteria.predicate.AndPredicate;
import org.luncert.filtersquery.api.criteria.predicate.OrPredicate;

public interface Predicate extends Node {

  static Predicate and(Predicate... predicates) {
    if (predicates.length < 2) {
      throw new IllegalArgumentException("expect at least 2 predicates");
    }
    return new AndPredicate(predicates);
  }

  static Predicate and(List<Predicate> predicates) {
    if (predicates.size() < 2) {
      throw new IllegalArgumentException("expect at least 2 predicates");
    }
    return new AndPredicate(predicates.toArray(new Predicate[0]));
  }

  static Predicate or(Predicate... predicates) {
    if (predicates.length < 2) {
      throw new IllegalArgumentException("expect at least 2 predicates");
    }
    return new OrPredicate(predicates);
  }

  static Predicate or(List<Predicate> predicates) {
    if (predicates.size() < 2) {
      throw new IllegalArgumentException("expect at least 2 predicates");
    }
    return new OrPredicate(predicates.toArray(new Predicate[0]));
  }

  // static Predicate not(Predicate predicate) {
  //   return new NotPredicate(predicate);
  // }
}
