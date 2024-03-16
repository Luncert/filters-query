package org.luncert.filtersquery.api.criteria.spec;

import org.luncert.filtersquery.api.criteria.Predicate;

public interface IFilterBySpec {

  ISortByAndPaginationAndBuildSpec filterBy(Predicate predicate);
}
