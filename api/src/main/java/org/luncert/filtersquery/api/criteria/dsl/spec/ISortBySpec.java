package org.luncert.filtersquery.api.criteria.dsl.spec;

import org.luncert.filtersquery.api.criteria.Sort;

public interface ISortBySpec {

  IOffsetSpec sortBy(Sort... sorts);
}
