package org.luncert.filtersquery.predicateimpl.builder;

import java.util.List;
import java.util.function.Predicate;

public class IntPredicateBuilder extends BasicTypedPredicateBuilder<Object> {

  @Override
  public Predicate<Object> equal(String name, String literalValue) {
    return obj -> compare(obj, name, Integer.parseInt(literalValue)) == 0;
  }

  @Override
  public Predicate<Object> notEqual(String name, String literalValue) {
    return obj -> compare(obj, name, Integer.parseInt(literalValue)) != 0;
  }

  @Override
  public Predicate<Object> in(String name, List<String> literalValues) {
    return obj -> literalValues.stream().anyMatch(v -> compare(obj, name, Integer.parseInt(v)) == 0);
  }

  @Override
  public Predicate<Object> greaterEqualThan(String name, String literalValue) {
    return obj -> compare(obj, name, Integer.parseInt(literalValue),
        (r, hasNull) -> !hasNull && r >= 0);
  }

  @Override
  public Predicate<Object> greaterThan(String name, String literalValue) {
    return obj -> compare(obj, name, Integer.parseInt(literalValue),
        (r, hasNull) -> !hasNull && r > 0);
  }

  @Override
  public Predicate<Object> lessEqualThan(String name, String literalValue) {
    return obj -> compare(obj, name, Integer.parseInt(literalValue),
        (r, hasNull) -> !hasNull && r <= 0);
  }

  @Override
  public Predicate<Object> lessThan(String name, String literalValue) {
    return obj -> compare(obj, name, Integer.parseInt(literalValue),
        (r, hasNull) -> !hasNull && r < 0);
  }

  @Override
  public Predicate<Object> between(String name, String startValue, String endValue) {
    return obj -> compare(obj, name, Integer.parseInt(startValue)) >= 0
        && compare(obj, name, Integer.parseInt(endValue)) <= 0;
  }
}
