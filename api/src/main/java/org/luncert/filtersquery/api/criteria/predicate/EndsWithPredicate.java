package org.luncert.filtersquery.api.criteria.predicate;

import java.util.List;
import org.luncert.filtersquery.api.criteria.Node;
import org.luncert.filtersquery.api.criteria.Reference;
import org.luncert.filtersquery.api.criteria.Value;

public class EndsWithPredicate extends PredicateWithReferenceAndSingleValue {

  public EndsWithPredicate(Reference ref, Value value) {
    super(ref, value);
  }

  @Override
  public String toString() {
    return ref + " endsWith " + value;
  }
}
