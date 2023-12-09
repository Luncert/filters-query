package org.lks.filtersquery.predicateimpl;

import java.util.function.Predicate;
import org.lks.filtersquery.api.FiltersQueryEngine;

public class PredicateFiltersQueryEngine extends FiltersQueryEngine {

  @SuppressWarnings("unchecked")
  public static <E> Predicate<E> buildQuery(String criteria, Class<E> entityType) {
    FiltersQueryBuilderPredicateImpl.ResultImpl result =
        buildQuery(criteria, new FiltersQueryBuilderPredicateImpl<E>(entityType));
    return (Predicate<E>) result.getPredicate();
  }
}
