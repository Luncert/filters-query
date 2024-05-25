package org.luncert.filtersquery.api.criteria.predicate;

import lombok.Getter;
import org.luncert.filtersquery.api.criteria.Node;
import org.luncert.filtersquery.api.criteria.Predicate;
import org.luncert.filtersquery.api.criteria.Reference;

@Getter
public abstract class PredicateWithReference implements Predicate {

  protected final Reference ref;

  protected PredicateWithReference(Reference ref) {
    this.ref = ref;
  }

  @Override
  public void insertChild(int idx, Node newChild) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Node removeChild(int idx) {
    throw new UnsupportedOperationException();
  }

  protected void checkIndex(int idx) {
    if (idx < 0 || idx >= getChildenSize()) {
      throw new IndexOutOfBoundsException(idx);
    }
  }
}
