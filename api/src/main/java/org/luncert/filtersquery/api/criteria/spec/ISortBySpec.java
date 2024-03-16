package org.luncert.filtersquery.api.criteria.spec;

import org.luncert.filtersquery.api.criteria.Sort;

public interface ISortBySpec {

  IOffsetSpec sortBy(Sort... sorts);
}
