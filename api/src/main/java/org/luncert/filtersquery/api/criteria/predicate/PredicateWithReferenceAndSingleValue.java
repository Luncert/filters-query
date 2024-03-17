package org.luncert.filtersquery.api.criteria.predicate;

import java.util.List;
import org.luncert.filtersquery.api.criteria.Node;
import org.luncert.filtersquery.api.criteria.Reference;
import org.luncert.filtersquery.api.criteria.Value;

public abstract class PredicateWithReferenceAndSingleValue extends PredicateWithReference {

  protected final Value value;

  protected PredicateWithReferenceAndSingleValue(Reference ref, Value value) {
    super(ref);
    this.value = value;
  }

  @Override
  public List<Node> getChildren() {
    return List.of(value);
  }
}
