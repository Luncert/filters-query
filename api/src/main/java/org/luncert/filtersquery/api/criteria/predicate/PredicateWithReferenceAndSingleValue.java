package org.luncert.filtersquery.api.criteria.predicate;

import org.luncert.filtersquery.api.criteria.Node;
import org.luncert.filtersquery.api.criteria.Reference;
import org.luncert.filtersquery.api.criteria.Value;

public abstract class PredicateWithReferenceAndSingleValue extends PredicateWithReference {

  protected Value value;

  protected PredicateWithReferenceAndSingleValue(Reference ref, Value value) {
    super(ref);
    this.value = value;
  }

  @Override
  public int getChildrenSize() {
    return 1;
  }

  @Override
  public Node getChild(int idx) {
    checkIndex(idx);
    return value;
  }

  @Override
  public void insertChild(int idx, Node newChild) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Node replaceChild(int idx, Node newChild) {
    checkIndex(idx);
    if (!(newChild instanceof Value)) {
      throw new IllegalArgumentException("node must be instance of Value");
    }
    var old = value;
    value =(Value) newChild;
    return old;
  }

  @Override
  public Node removeChild(int idx) {
    throw new UnsupportedOperationException();
  }
}
