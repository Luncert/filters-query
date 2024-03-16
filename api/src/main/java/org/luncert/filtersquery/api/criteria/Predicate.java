package org.luncert.filtersquery.api.criteria;

import org.luncert.filtersquery.api.criteria.predicate.AndPredicate;
import org.luncert.filtersquery.api.criteria.predicate.OrPredicate;

public interface Predicate {

  static Predicate and(Predicate predicate1, Predicate predicate2, Predicate...predicates) {
    Predicate[] arr = new Predicate[2 + predicates.length];
    arr[0] = predicate1;
    arr[1] = predicate2;
    System.arraycopy(predicates, 0, arr, 2, predicates.length);
    return new AndPredicate(arr);
  }

  static Predicate or(Predicate predicate1, Predicate predicate2, Predicate...predicates) {
    Predicate[] arr = new Predicate[2 + predicates.length];
    arr[0] = predicate1;
    arr[1] = predicate2;
    System.arraycopy(predicates, 0, arr, 2, predicates.length);
    return new OrPredicate(arr);
  }

  // static Predicate not(Predicate predicate) {
  //   return new NotPredicate(predicate);
  // }
}
