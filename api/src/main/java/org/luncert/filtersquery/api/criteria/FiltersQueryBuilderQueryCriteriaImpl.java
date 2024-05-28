package org.luncert.filtersquery.api.criteria;

import static org.luncert.filtersquery.api.criteria.Reference.ref;
import static org.luncert.filtersquery.api.criteria.Value.literal;
import static org.luncert.filtersquery.api.criteria.Value.number;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.luncert.filtersquery.api.BasicFiltersQueryBuilder;
import org.luncert.filtersquery.api.FiltersQueryBuilder;
import org.luncert.filtersquery.api.Utils;
import org.luncert.filtersquery.api.grammar.FiltersQueryParser;

/**
 * Parse criteria to FiltersQuery object.
 */
public class FiltersQueryBuilderQueryCriteriaImpl extends BasicFiltersQueryBuilder {

  private final Stack<Integer> parenStack = new Stack<>();
  private List<Predicate> predicates = new ArrayList<>();
  private final List<Sort> sorts = new ArrayList<>();
  private List<Integer> operations = new LinkedList<>();
  private Integer offset;
  private Integer limit;
  private final ResultImpl result = new ResultImpl();

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ResultImpl implements FiltersQueryBuilder.Result {

    private QueryCriteria queryCriteria;
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
  public void operator(Token operator) {
    operations.add(operator.getType());
  }

  @Override
  public void equal(String name, FiltersQueryParser.PropertyValueWithReferenceBoolNullContext value) {
    predicates.add(ref(name).is(parseValue(value)));
  }

  @Override
  public void notEqual(String name, FiltersQueryParser.PropertyValueWithReferenceBoolNullContext value) {
    predicates.add(ref(name).isNot(parseValue(value)));
  }

  @Override
  public void empty(String name) {
    predicates.add(ref(name).isEmpty());
  }

  @Override
  public void notEmpty(String name) {
    predicates.add(ref(name).notEmpty());
  }

  @Override
  public void in(String name, List<FiltersQueryParser.PropertyValueWithReferenceBoolNullContext> values) {
    predicates.add(ref(name).in(values.stream().map(this::parseValue).toArray(Value[]::new)));
  }

  @Override
  public void greaterThanEqual(String name, FiltersQueryParser.PropertyValueWithReferenceContext value) {
    predicates.add(ref(name).gte(parseValue(value)));
  }

  @Override
  public void greaterThan(String name, FiltersQueryParser.PropertyValueWithReferenceContext value) {
    predicates.add(ref(name).gt(parseValue(value)));
  }

  @Override
  public void lessThanEqual(String name, FiltersQueryParser.PropertyValueWithReferenceContext value) {
    predicates.add(ref(name).lte(parseValue(value)));
  }

  @Override
  public void lessThan(String name, FiltersQueryParser.PropertyValueWithReferenceContext value) {
    predicates.add(ref(name).lt(parseValue(value)));
  }

  @Override
  public void between(String name, FiltersQueryParser.PropertyValueContext startValue, FiltersQueryParser.PropertyValueContext endValue) {
    predicates.add(ref(name).between(parseValue(startValue), parseValue(endValue)));
  }

  @Override
  public void startsWith(String name, FiltersQueryParser.StringPropertyValueContext value) {
    predicates.add(ref(name).startsWith(parseValue(value)));
  }

  @Override
  public void endsWith(String name, FiltersQueryParser.StringPropertyValueContext value) {
    predicates.add(ref(name).endsWith(parseValue(value)));
  }

  @Override
  public void like(String name, FiltersQueryParser.StringPropertyValueContext value) {
    predicates.add(ref(name).like(parseValue(value)));
  }

  @Override
  public void order(String name, Token direction) {
    sorts.add(new Sort(name, getTokenName(direction).equals("ASC") ? Order.ASC : Order.DESC));
  }

  @Override
  public void offset(int offset) {
    this.offset = offset;
  }

  @Override
  public void limit(int limit) {
    this.limit = limit;
  }

  @Override
  public void end() {
    Predicate predicate = mergePredicates(0);
    result.queryCriteria = new QueryCriteria(predicate, new Sorts(sorts), offset, limit);
  }

  @Override
  @SuppressWarnings("unchecked")
  public ResultImpl build() {
    return result;
  }

  protected Value parseValue(ParseTree value) {
    //if (value instanceof FiltersQueryParser.PropertyValueWithReferenceBoolNullContext) {
    //  return Value.NULL;
    //}

    var s = resolveStringLiteral(value);
    if (s != null) {
      return literal(Utils.unwrap(s, '"'));
    }

    s = resolveDecimalLiteral(value);
    if (s != null) {
      return number(Long.valueOf(s));
    }

    return number(Double.valueOf(value.getText()));
  }

  protected Predicate mergePredicates(int startFrom) {
    if (predicates.isEmpty()) {
      return null;
    }
    Predicate predicate = predicates.get(startFrom);
    for (int i = startFrom + 1; i < predicates.size(); i++) {
      if (getTokenName(operations.get(i - 1)).equals("LOGICAL_AND")) {
        predicate = Predicate.and(predicate, predicates.get(i));
      } else {
        predicate = Predicate.or(predicate, predicates.get(i));
      }
    }
    predicates = predicates.subList(0, startFrom);
    operations = operations.subList(0, startFrom);
    predicates.add(predicate);
    return predicate;
  }
}
