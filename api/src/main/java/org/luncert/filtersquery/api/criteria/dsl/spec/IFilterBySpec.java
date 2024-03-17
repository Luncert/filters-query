package org.luncert.filtersquery.api.criteria.dsl.spec;

import org.luncert.filtersquery.api.criteria.Predicate;

public interface IFilterBySpec {

  ISortByAndPaginationAndBuildSpec filterBy(Predicate predicate);
}
