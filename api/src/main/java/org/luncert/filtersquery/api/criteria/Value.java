package org.luncert.filtersquery.api.criteria;

public interface Value {

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
  }

  record NumberValue(Number value) implements Value {

    @Override
    public String toString() {
      return value.toString();
    }
  }
}
