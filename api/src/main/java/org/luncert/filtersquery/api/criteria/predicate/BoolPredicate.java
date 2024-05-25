package org.luncert.filtersquery.api.criteria.predicate;

import lombok.AllArgsConstructor;
import org.luncert.filtersquery.api.criteria.Node;
import org.luncert.filtersquery.api.criteria.Predicate;

@AllArgsConstructor
public abstract class BoolPredicate implements Predicate {

  protected Predicate[] predicates;

  protected abstract String getOperator();

  @Override
  public boolean isBoolExpression() {
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("(");
    builder.append(predicates[0]);
    for (int i = 1; i < predicates.length; i++) {
      Predicate predicate = predicates[i];
      builder.append(" ").append(getOperator()).append(" ").append(predicate);
    }
    return builder.append(")").toString();
  }

  @Override
  public int getChildenSize() {
    return predicates.length;
  }

  @Override
  public Node getChild(int idx) {
    return predicates[idx];
  }

  @Override
  public void insertChild(int idx, Node newChild) {
    checkIndex(idx);
    if (!(newChild instanceof Predicate)) {
      throw new IllegalArgumentException("child must be instance of predicate");
    }

    var newArr = new Predicate[predicates.length + 1];
    System.arraycopy(predicates, 0, newArr, 0, idx);
    System.arraycopy(predicates, idx + 1, newArr, idx, predicates.length - idx);
    newArr[idx] = (Predicate) newChild;
    predicates = newArr;
  }

  @Override
  public Node replaceChild(int idx, Node newChild) {
    checkIndex(idx);
    if (!(newChild instanceof Predicate)) {
      throw new IllegalArgumentException("child must be instance of predicate");
    }
    var tmp = predicates[idx];
    predicates[idx] = (Predicate) newChild;
    return tmp;
  }

  @Override
  public Node removeChild(int idx) {
    checkIndex(idx);

    var newArr = new Predicate[predicates.length - 1];
    System.arraycopy(predicates, 0, newArr, 0, idx);
    System.arraycopy(predicates, idx + 1, newArr, idx, newArr.length - idx);

    var target = predicates[idx];
    predicates = newArr;
    return target;
  }

  protected void checkIndex(int idx) {
    if (idx < 0 || idx >= getChildenSize()) {
      throw new IndexOutOfBoundsException(idx);
    }
  }
}
