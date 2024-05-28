package org.luncert.filtersquery.api.criteria.dsl.spec;

import java.util.List;
import org.luncert.filtersquery.api.criteria.Predicate;

public interface IFilterBySpec {

  IFilterBySpec associates(List<String> associateTargets);

  ISortByAndPaginationAndBuildSpec filterBy(Predicate predicate);
}
