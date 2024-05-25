package org.luncert.filtersquery.api.criteria;

import java.util.List;

public interface Node {

  int getChildenSize();

  Node getChild(int idx);

  void insertChild(int idx, Node newChild);

  Node replaceChild(int idx, Node newChild);

  Node removeChild(int idx);

  @SuppressWarnings("unchecked")
  default <T extends Node> T as(Class<T> type) {
    return (T) this;
  }
}
