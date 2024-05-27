package org.luncert.filtersquery.api.criteria.predicate;

import org.luncert.filtersquery.api.criteria.Node;
import org.luncert.filtersquery.api.criteria.Reference;
import org.luncert.filtersquery.api.criteria.Value;

public class BetweenPredicate extends PredicateWithReference {

  private Value value1;
  private Value value2;

  public BetweenPredicate(Reference ref, Value value1, Value value2) {
    super(ref);
    this.value1 = value1;
    this.value2 = value2;
  }

  @Override
  public String toString() {
    return ref.name() + " between [" + value1 + ", " + value2 + "]";
  }

  @Override
  public int getChildrenSize() {
    return 2;
  }

  @Override
  public Node getChild(int idx) {
    checkIndex(idx);
    return idx == 0 ? value1 : value2;
  }

  @Override
  public Node replaceChild(int idx, Node newChild) {
    checkIndex(idx);
    if (!(newChild instanceof Value)) {
      throw new IllegalArgumentException("child must be instance of predicate");
    }
    Value tmp;
    if (idx == 0) {
      tmp = value1;
      value1 = (Value) newChild;
    } else {
      tmp = value2;
      value2 = (Value) newChild;
    }
    return tmp;
  }

  @Override
  public Node removeChild(int idx) {
    throw new UnsupportedOperationException();
  }
}
