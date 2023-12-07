package org.lks.filtersquery.luceneimpl;

import org.lks.filtersquery.api.FiltersQueryEngine;

public class LuceneFiltersQueryEngine extends FiltersQueryEngine {

  public FiltersQueryBuilderLuceneImpl.ResultImpl buildQuery(String criteria, Class<?> entityType) {
    return buildQuery(criteria, new FiltersQueryBuilderLuceneImpl(entityType));
  }
}
