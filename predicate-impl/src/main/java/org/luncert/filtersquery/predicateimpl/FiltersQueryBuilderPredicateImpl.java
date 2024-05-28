package org.luncert.filtersquery.predicateimpl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.luncert.filtersquery.api.BasicFiltersQueryBuilder;
import org.luncert.filtersquery.api.FiltersQueryBuilder;
import org.luncert.filtersquery.api.exception.UnsupportedSyntaxException;
import org.luncert.filtersquery.api.grammar.FiltersQueryParser;
import org.luncert.filtersquery.predicateimpl.builder.TypedPredicateBuilder;
import org.luncert.filtersquery.predicateimpl.builder.TypedPredicateBuilders;
import org.luncert.filtersquery.predicateimpl.exception.FiltersQueryPredicateBuilderException;

@RequiredArgsConstructor
public class FiltersQueryBuilderPredicateImpl<E> extends BasicFiltersQueryBuilder {

  private final Class<?> entityType;

  private final Stack<Integer> parenStack = new Stack<>();
  private List<Predicate<E>> predicates = new ArrayList<>();
  private List<Integer> operations = new LinkedList<>();
  private final ResultImpl result = new ResultImpl();

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ResultImpl implements FiltersQueryBuilder.Result {

    private Predicate<?> predicate;
  }

  @Override
  public void enterParentheses() {
    parenStack.push(predicates.size());
  }

  @Override
  public void exitParentheses() {
    int startFrom = parenStack.pop();
    // construct predicate
    mergePredicates(startFrom);
  }

  @Override
  public void operator(Token operator, boolean inConjunction) {
    operations.add(operator.getType());
  }

  @Override
  public void equal(String name, FiltersQueryParser.PropertyValueWithReferenceBoolNullContext value) {
    predicates.add(getTypeMetadata(name).equal(name, getLiteral(value)));
  }

  @Override
  public void notEqual(String name, FiltersQueryParser.PropertyValueWithReferenceBoolNullContext value) {
    predicates.add(getTypeMetadata(name).notEqual(name, getLiteral(value)));
  }

  @Override
  public void empty(String name) {
    predicates.add(getTypeMetadata(name).empty(name));
  }

  @Override
  public void notEmpty(String name) {
    predicates.add(getTypeMetadata(name).notEmpty(name));
  }

  @Override
  public void in(String name, List<FiltersQueryParser.PropertyValueWithReferenceBoolNullContext> values) {
    predicates.add(getTypeMetadata(name)
        .in(name, values.stream().map(this::getLiteral).toList()));
  }

  @Override
  public void greaterThanEqual(String name, FiltersQueryParser.PropertyValueWithReferenceContext value) {
    predicates.add(getTypeMetadata(name).greaterEqualThan(name, getLiteral(value)));
  }

  @Override
  public void greaterThan(String name, FiltersQueryParser.PropertyValueWithReferenceContext value) {
    predicates.add(getTypeMetadata(name).greaterThan(name, getLiteral(value)));
  }

  @Override
  public void lessThanEqual(String name, FiltersQueryParser.PropertyValueWithReferenceContext value) {
    predicates.add(getTypeMetadata(name).lessEqualThan(name, getLiteral(value)));
  }

  @Override
  public void lessThan(String name, FiltersQueryParser.PropertyValueWithReferenceContext value) {
    predicates.add(getTypeMetadata(name).lessThan(name, getLiteral(value)));
  }

  @Override
  public void between(String name, FiltersQueryParser.PropertyValueContext startValue, FiltersQueryParser.PropertyValueContext endValue) {
    predicates.add(getTypeMetadata(name).between(
        name, getLiteral(startValue), getLiteral(endValue)));
  }

  @Override
  public void startsWith(String name, FiltersQueryParser.StringPropertyValueContext value) {
    predicates.add(getTypeMetadata(name).startsWith(name, getLiteral(value)));
  }

  @Override
  public void endsWith(String name, FiltersQueryParser.StringPropertyValueContext value) {
    predicates.add(getTypeMetadata(name).endsWith(name, getLiteral(value)));
  }

  @Override
  public void like(String name, FiltersQueryParser.StringPropertyValueContext value) {
    predicates.add(getTypeMetadata(name).like(name, getLiteral(value)));
  }

  @Override
  public void order(String name, Token direction) {
    throw new UnsupportedSyntaxException("sort by");
  }

  @Override
  public void offset(int offset) {
    throw new UnsupportedSyntaxException("offset");
  }

  @Override
  public void limit(int limit) {
    throw new UnsupportedSyntaxException("limit");
  }

  @Override
  public void end() {
    result.predicate = mergePredicates(0);
    if (result.predicate == null) {
      result.predicate = obj -> true;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public ResultImpl build() {
    return result;
  }

  @SuppressWarnings("unchecked")
  private TypedPredicateBuilder<E> getTypeMetadata(String name) {
    try {
      return (TypedPredicateBuilder<E>) TypedPredicateBuilders.get(
          entityType.getDeclaredField(name).getType());
    } catch (NoSuchFieldException e) {
      throw new FiltersQueryPredicateBuilderException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private Predicate<E> mergePredicates(int startFrom) {
    if (predicates.isEmpty()) {
      return null;
    }

    final int len = predicates.size() - startFrom;
    Predicate<E>[] toMerge = (Predicate<E>[]) new Predicate<?>[len];
    int[] ops = new int[len - 1];
    for (int i = startFrom, limit = predicates.size() - 1; i < limit; i++) {
      toMerge[i - startFrom] = predicates.get(i);
      ops[i - startFrom] = operations.get(i);
    }
    toMerge[len - 1] = predicates.get(predicates.size() - 1);
    predicates = predicates.subList(0, startFrom);
    operations = operations.subList(0, startFrom);

    Predicate<E> predicate = obj -> {
      boolean r = toMerge[0].test(obj);
      for (int i = 1; i < len; i++) {
        if (getTokenName(ops[i - 1]).equals("LOGICAL_AND")) {
          r = r && toMerge[i].test(obj);
        } else {
          r = r || toMerge[i].test(obj);
        }
      }
      return r;
    };

    predicates.add(predicate);
    return predicate;
  }
}
