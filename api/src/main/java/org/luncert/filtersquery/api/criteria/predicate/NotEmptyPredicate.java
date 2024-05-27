package org.luncert.filtersquery.api.criteria.predicate;

import org.luncert.filtersquery.api.criteria.Node;
import org.luncert.filtersquery.api.criteria.Reference;

public class NotEmptyPredicate extends PredicateWithReference {

  public NotEmptyPredicate(Reference ref) {
    super(ref);
  }

  @Override
  public String toString() {
    return ref + " != empty";
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
  public Node replaceChild(int idx, Node newChild) {
    throw new UnsupportedOperationException();
  }
}
