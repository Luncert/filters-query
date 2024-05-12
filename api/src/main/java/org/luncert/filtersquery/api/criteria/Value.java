package org.luncert.filtersquery.api.criteria;

import java.util.Collections;
import java.util.List;

public interface Value extends Node {

  public static final Value NULL = new NullValue();

  static LiteralValue literal(String value) {
    return new LiteralValue(value);
  }

  static NumberValue number(Number value) {
    return new NumberValue(value);
  }

  record LiteralValue(String value) implements Value {

    @Override
    public String toString() {
      return "\"" + value + "\"";
    }

    @Override
    public List<Node> getChildren() {
      return Collections.emptyList();
    }
  }

  record NumberValue(Number value) implements Value {

    @Override
    public String toString() {
      return value.toString();
    }

    @Override
    public List<Node> getChildren() {
      return Collections.emptyList();
    }
  }

  class NullValue implements Value {

    @Override
    public String toString() {
      return "null";
    }

    @Override
    public List<Node> getChildren() {
      return Collections.emptyList();
    }
  }
}
