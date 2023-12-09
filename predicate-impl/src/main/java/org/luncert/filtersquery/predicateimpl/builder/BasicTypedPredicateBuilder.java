package org.luncert.filtersquery.predicateimpl.builder;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;

public abstract class BasicTypedPredicateBuilder<E> implements TypedPredicateBuilder<E> {

  private static final int CONTAINS_NULL = Integer.MAX_VALUE;

  @Override
  public Predicate<E> empty(String name) {
    return obj -> isEmpty(obj, name);
  }

  @Override
  public Predicate<E> notEmpty(String name) {
    return obj -> isNotEmpty(obj, name);
  }

  @Override
  public Predicate<E> equal(String name, String literalValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Predicate<E> notEqual(String name, String literalValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Predicate<E> greaterEqualThan(String name, String literalValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Predicate<E> greaterThan(String name, String literalValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Predicate<E> lessEqualThan(String name, String literalValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Predicate<E> lessThan(String name, String literalValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Predicate<E> between(String name, String startValue, String endValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Predicate<E> startsWith(String name, String literalValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Predicate<E> endsWith(String name, String literalValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Predicate<E> like(String name, String literalValue) {
    throw new UnsupportedOperationException();
  }

  protected boolean isEmpty(E obj, String name) {
    Object fieldValue = getFieldValue(obj, name);
    if (fieldValue == null) {
      return true;
    }
    return String.class.isAssignableFrom(fieldValue.getClass())
        && StringUtils.isEmpty((String) fieldValue);
  }

  protected boolean isNotEmpty(E obj, String name) {
    Object fieldValue = getFieldValue(obj, name);
    if (fieldValue == null) {
      return false;
    }
    return !String.class.isAssignableFrom(fieldValue.getClass())
        || StringUtils.isNotEmpty((String) fieldValue);
  }

  protected boolean compare(E obj, String name, Double target, BiPredicate<Integer, Boolean> predicate) {
    int r = compare(obj, name, target);
    return predicate.test(r, r == CONTAINS_NULL);
  }

  protected int compare(E obj, String name, Double target) {
    Double fieldValue = getFieldValue(obj, name);
    return compare(fieldValue, target, Double::compareTo);
  }

  protected boolean compare(E obj, String name, Float target, BiPredicate<Integer, Boolean> predicate) {
    int r = compare(obj, name, target);
    return predicate.test(r, r == CONTAINS_NULL);
  }

  protected int compare(E obj, String name, Float target) {
    Float fieldValue = getFieldValue(obj, name);
    return compare(fieldValue, target, Float::compareTo);
  }

  protected boolean compare(E obj, String name, Integer target, BiPredicate<Integer, Boolean> predicate) {
    int r = compare(obj, name, target);
    return predicate.test(r, r == CONTAINS_NULL);
  }

  protected int compare(E obj, String name, Integer target) {
    Integer fieldValue = getFieldValue(obj, name);
    return compare(fieldValue, target, Integer::compareTo);
  }

  protected boolean compare(E obj, String name, Long target, BiPredicate<Integer, Boolean> predicate) {
    int r = compare(obj, name, target);
    return predicate.test(r, r == CONTAINS_NULL);
  }

  protected int compare(E obj, String name, Long target) {
    Long fieldValue = getFieldValue(obj, name);
    return compare(fieldValue, target, Long::compareTo);
  }

  protected boolean compare(E obj, String name, String target, BiPredicate<Integer, Boolean> predicate) {
    int r = compare(obj, name, target);
    return predicate.test(r, r == CONTAINS_NULL);
  }

  protected int compare(E obj, String name, String target) {
    String fieldValue = getFieldValue(obj, name);
    return compare(fieldValue, target, String::compareTo);
  }

  protected <T> int compare(T a, T b, Comparator<T> comparator) {
    if (a == null) {
      if (b == null) {
        return 0;
      }
      return CONTAINS_NULL;
    } else if (b == null) {
      return CONTAINS_NULL;
    } else {
      return comparator.compare(a, b);
    }
  }

  @SuppressWarnings("unchecked")
  protected <T> T getFieldValue(E obj, String name) {
    try {
      Field field = obj.getClass().getDeclaredField(name);
      field.setAccessible(true);
      return (T) field.get(obj);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
