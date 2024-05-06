package org.luncert.filtersquery.predicateimpl.builder;

import java.util.List;
import java.util.function.Predicate;

public class StringPredicateBuilder extends BasicTypedPredicateBuilder<Object> {

  @Override
  public Predicate<Object> equal(String name, String literalValue) {
    return obj -> compare(obj, name, literalValue) == 0;
  }

  @Override
  public Predicate<Object> notEqual(String name, String literalValue) {
    return obj -> compare(obj, name, literalValue) != 0;
  }

  @Override
  public Predicate<Object> in(String name, List<String> literalValues) {
    return obj -> literalValues.stream().anyMatch(v -> compare(obj, name, v) == 0);
  }

  @Override
  public Predicate<Object> greaterEqualThan(String name, String literalValue) {
    return obj -> compare(obj, name, literalValue, (r, hasNull) -> !hasNull && r >= 0);
  }

  @Override
  public Predicate<Object> greaterThan(String name, String literalValue) {
    return obj -> compare(obj, name, literalValue, (r, hasNull) -> !hasNull && r > 0);
  }

  @Override
  public Predicate<Object> lessEqualThan(String name, String literalValue) {
    return obj -> compare(obj, name, literalValue, (r, hasNull) -> !hasNull && r <= 0);
  }

  @Override
  public Predicate<Object> lessThan(String name, String literalValue) {
    return obj -> compare(obj, name, literalValue, (r, hasNull) -> !hasNull && r < 0);
  }

  @Override
  public Predicate<Object> between(String name, String startValue, String endValue) {
    return obj -> compare(obj, name, startValue, (r, hasNull) -> !hasNull && r >= 0)
        && compare(obj, name, endValue, (r, hasNull) -> !hasNull && r <= 0);
  }

  @Override
  public Predicate<Object> startsWith(String name, String literalValue) {
    return obj -> {
      String fieldValue = getFieldValue(obj, name);
      return fieldValue != null && fieldValue.startsWith(literalValue);
    };
  }

  @Override
  public Predicate<Object> endsWith(String name, String literalValue) {
    return obj -> {
      String fieldValue = getFieldValue(obj, name);
      return fieldValue != null && fieldValue.endsWith(literalValue);
    };
  }

  @Override
  public Predicate<Object> like(String name, String literalValue) {
    return obj -> {
      String fieldValue = getFieldValue(obj, name);
      return fieldValue != null && fieldValue.matches(".*?" + literalValue + ".*?");
    };
  }
}
