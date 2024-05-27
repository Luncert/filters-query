package org.luncert.filtersquery.api.criteria;

public interface Node {

  int getChildrenSize();

  default Node[] getChildren() {
    var arr = new Node[getChildrenSize()];
    for (int i = 0; i < getChildrenSize(); i++) {
      arr[i] = getChild(i);
    }
    return arr;
  }

  Node getChild(int idx);

  void insertChild(int idx, Node newChild);

  Node replaceChild(int idx, Node newChild);

  Node removeChild(int idx);

  @SuppressWarnings("unchecked")
  default <T extends Node> T as(Class<T> type) {
    return (T) this;
  }
}
