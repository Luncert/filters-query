package org.luncert.filtersquery.api.criteria;

import java.util.Objects;

public abstract class Value implements Node {

  public static final Value NULL = new Value() {
    @Override
    public String toString() {
      return "null";
    }
  };

  public static final Value TRUE = new Value() {
    @Override
    public String toString() {
      return Boolean.TRUE.toString();
    }
  };

  public static final Value FALSE = new Value() {
    @Override
    public String toString() {
      return Boolean.FALSE.toString();
    }
  };

  public static LiteralValue literal(String value) {
    return new LiteralValue(value);
  }

  public static NumberValue number(Number value) {
    return new NumberValue(value);
  }

  @Override
  public int getChildrenSize() {
    return 0;
  }

  @Override
  public Node getChild(int idx) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void insertChild(int idx, Node newChild) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Node replaceChild(int idx, Node newChild) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Node removeChild(int idx) {
    throw new UnsupportedOperationException();
  }

  public static final class LiteralValue extends Value {
    private final String value;

    LiteralValue(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return "\"" + value + "\"";
    }

    public String value() {
      return value;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) return true;
      if (obj == null || obj.getClass() != this.getClass()) return false;
      var that = (LiteralValue) obj;
      return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }

  }

  public static final class NumberValue extends Value {

    private final Number value;

    NumberValue(Number value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value.toString();
    }

    public Number value() {
      return value;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) return true;
      if (obj == null || obj.getClass() != this.getClass()) return false;
      var that = (NumberValue) obj;
      return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }
  }
}
