package org.luncert.filtersquery.api.criteria.predicate;

import java.util.Collections;
import java.util.List;
import org.luncert.filtersquery.api.criteria.Node;
import org.luncert.filtersquery.api.criteria.Predicate;
import org.luncert.filtersquery.api.criteria.Reference;

public abstract class PredicateWithReference implements Predicate {

  protected final Reference ref;

  protected PredicateWithReference(Reference ref) {
    this.ref = ref;
  }

  @Override
  public List<Node> getChildren() {
    return Collections.emptyList();
  }
}
