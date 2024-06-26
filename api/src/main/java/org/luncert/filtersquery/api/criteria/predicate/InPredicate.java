package org.luncert.filtersquery.api.criteria.predicate;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.luncert.filtersquery.api.criteria.Node;
import org.luncert.filtersquery.api.criteria.Reference;
import org.luncert.filtersquery.api.criteria.Value;

public class InPredicate extends PredicateWithReference {

  private final Value[] values;

  public InPredicate(Reference ref, Value...values) {
    super(ref);
    this.values = values;
  }

  @Override
  public String toString() {
    return ref + " in [" + Arrays.stream(values).map(Object::toString).collect(Collectors.joining(",")) + "]";
  }

  @Override
  public int getChildrenSize() {
    return values.length;
  }

  @Override
  public Node getChild(int idx) {
    checkIndex(idx);
    return values[idx];
  }

  @Override
  public Node replaceChild(int idx, Node newChild) {
    checkIndex(idx);
    if (!(newChild instanceof Value)) {
      throw new IllegalArgumentException("node must be instance of Value");
    }
    var old = values[idx];
    values[idx] = (Value) newChild;
    return old;
  }
}
