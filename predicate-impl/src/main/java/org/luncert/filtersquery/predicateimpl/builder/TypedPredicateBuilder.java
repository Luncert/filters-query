package org.luncert.filtersquery.predicateimpl.builder;

import java.util.List;
import java.util.function.Predicate;

public interface TypedPredicateBuilder<T> {

  Predicate<T> equal(String name, String literalValue);

  Predicate<T> notEqual(String name, String literalValue);

  Predicate<T> empty(String name);

  Predicate<T> notEmpty(String name);

  Predicate<T> in(String name, List<String> literalValues);

  Predicate<T> greaterEqualThan(String name, String literalValue);

  Predicate<T> greaterThan(String name, String literalValue);

  Predicate<T> lessEqualThan(String name, String literalValue);

  Predicate<T> lessThan(String name, String literalValue);

  Predicate<T> between(String name, String startValue, String endValue);

  Predicate<T> startsWith(String name, String literalValue);

  Predicate<T> endsWith(String name, String literalValue);

  Predicate<T> like(String name, String literalValue);
}
