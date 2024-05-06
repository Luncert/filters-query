package org.luncert.filtersquery.api.criteria;

import org.luncert.filtersquery.api.criteria.predicate.BetweenPredicate;
import org.luncert.filtersquery.api.criteria.predicate.EndsWithPredicate;
import org.luncert.filtersquery.api.criteria.predicate.EqualsPredicate;
import org.luncert.filtersquery.api.criteria.predicate.GreaterThanEqualsPredicate;
import org.luncert.filtersquery.api.criteria.predicate.GreaterThanPredicate;
import org.luncert.filtersquery.api.criteria.predicate.InPredicate;
import org.luncert.filtersquery.api.criteria.predicate.IsEmptyPredicate;
import org.luncert.filtersquery.api.criteria.predicate.LessThanEqualsPredicate;
import org.luncert.filtersquery.api.criteria.predicate.LessThanPredicate;
import org.luncert.filtersquery.api.criteria.predicate.LikePredicate;
import org.luncert.filtersquery.api.criteria.predicate.NotEmptyPredicate;
import org.luncert.filtersquery.api.criteria.predicate.NotEqualsPredicate;
import org.luncert.filtersquery.api.criteria.predicate.StartsWithPredicate;

public record Reference(String name) {

  public static Reference ref(String name) {
    return new Reference(name);
  }

  @Override
  public String toString() {
    return name;
  }

  public Predicate between(Value a, Value b) {
    return new BetweenPredicate(this, a, b);
  }

  public Predicate isEmpty() {
    return new IsEmptyPredicate(this);
  }

  public Predicate notEmpty() {
    return new NotEmptyPredicate(this);
  }

  public Predicate startsWith(Value value) {
    return new StartsWithPredicate(this, value);
  }

  public Predicate endsWith(Value value) {
    return new EndsWithPredicate(this, value);
  }

  public Predicate like(Value value) {
    return new LikePredicate(this, value);
  }

  public Predicate is(Value value) {
    return new EqualsPredicate(this, value);
  }

  public Predicate isNot(Value value) {
    return new NotEqualsPredicate(this, value);
  }

  public Predicate gt(Value value) {
    return new GreaterThanPredicate(this, value);
  }

  public Predicate gte(Value value) {
    return new GreaterThanEqualsPredicate(this, value);
  }

  public Predicate lt(Value value) {
    return new LessThanPredicate(this, value);
  }

  public Predicate lte(Value value) {
    return new LessThanEqualsPredicate(this, value);
  }

  public Predicate in(Value... values) {
    return new InPredicate(this, values);
  }

  public Sort asc() {
    return new Sort(name, Order.ASC);
  }

  public Sort desc() {
    return new Sort(name, Order.DESC);
  }
}
