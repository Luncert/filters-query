package org.luncert.filtersquery.api.criteria;

import java.util.List;

public interface Node {

  List<Node> getChildren();

  @SuppressWarnings("unchecked")
  default <T extends Node> T as(Class<T> type) {
    return (T) this;
  }
}
