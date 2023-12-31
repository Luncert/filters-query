package org.luncert.filtersquery.jpaimpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Parameter;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.luncert.filtersquery.api.BasicFiltersQueryBuilder;
import org.luncert.filtersquery.api.FiltersQueryBuilder;
import org.luncert.filtersquery.api.Utils;
import org.luncert.filtersquery.api.exception.IllegalParameterException;

@RequiredArgsConstructor
public class FiltersQueryBuilderJpaImpl<E> extends BasicFiltersQueryBuilder {

  private final EntityManager em;
  private final CriteriaBuilder criteriaBuilder;
  private final CriteriaQuery<Tuple> criteriaQuery;
  private final Root<E> entity;

  private final Stack<Integer> parenStack = new Stack<>();
  private List<Predicate> predicates = new ArrayList<>();
  private List<Integer> operations = new LinkedList<>();
  private final List<Order> orders = new ArrayList<>();
  private final Map<Parameter<?>, String> parameters = new HashMap<>();
  private final ResultImpl result = new ResultImpl();

  public void enterParentheses() {
    parenStack.push(predicates.size());
  }

  public void exitParentheses() {
    int startFrom = parenStack.pop();
    // construct predicate
    mergePredicates(startFrom);
  }

  public void operator(Token operator) {
    operations.add(operator.getType());
  }

  public void equal(String name, ParseTree value) {
    predicates.add(createPredicate(name, value, criteriaBuilder::equal));
  }

  /**
   * Assert column not equal to specified value, not null and not empty.
   */
  public void notEqual(String name, ParseTree value) {
    Predicate predicate = createPredicate(name, value, (path, parameter) -> {
      if (isLiteral(name)) {
        return criteriaBuilder.or(
            criteriaBuilder.notEqual(path, parameter),
            criteriaBuilder.isNull(path),
            criteriaBuilder.equal(criteriaBuilder.length(path.as(String.class)), 0)
        );
      } else {
        return criteriaBuilder.or(
            criteriaBuilder.notEqual(path, parameter),
            criteriaBuilder.isNull(path)
        );
      }
    });
    predicates.add(predicate);
  }

  /**
   * Assert column not null and not empty.
   */
  public void empty(String name) {
    Path<?> path = entity.get(name);
    Predicate predicate = criteriaBuilder.isNull(path);
    if (isLiteral(name)) {
      predicate = criteriaBuilder.or(predicate,
          criteriaBuilder.equal(criteriaBuilder.length(path.as(String.class)), 0));
    }
    predicates.add(predicate);
  }

  public void notEmpty(String name) {
    Path<?> path = entity.get(name);
    Predicate predicate = criteriaBuilder.isNotNull(path);
    if (isLiteral(name)) {
      predicate = criteriaBuilder.and(predicate,
          criteriaBuilder.greaterThan(criteriaBuilder.length(path.as(String.class)), 0));
    }
    predicates.add(predicate);
  }

  public void greaterEqualThan(String name, ParseTree value) {
    predicates.add(createPredicate(name, value, criteriaBuilder::greaterThanOrEqualTo));
  }

  public void greaterThan(
      String name, ParseTree value) {
    predicates.add(createPredicate(name, value, criteriaBuilder::greaterThan));
  }

  public void lessEqualThan(
      String name, ParseTree value) {
    predicates.add(createPredicate(name, value, criteriaBuilder::lessThanOrEqualTo));
  }

  public void lessThan(
      String name, ParseTree value) {
    predicates.add(createPredicate(name, value, criteriaBuilder::lessThan));
  }

  public void between(
      String name, ParseTree startValue, ParseTree endValue) {
    Path<? extends Comparable<? super Object>> path = entity.get(name);
    ParameterExpression<? extends Comparable<? super Object>> p1 =
        createParameter(name, startValue);
    ParameterExpression<? extends Comparable<? super Object>> p2 = createParameter(name, endValue);
    Predicate predicate = criteriaBuilder.between(path, p1, p2);
    predicates.add(predicate);
  }

  public void startsWith(
      String name, ParseTree value) {
    Path<?> path = entity.get(name);
    ParameterExpression<String> parameter = createParameter(name, value, literal -> literal + "%%");
    Predicate predicate = criteriaBuilder.like(path.as(String.class), parameter, '\\');
    predicates.add(predicate);
  }

  public void endsWith(
      String name, ParseTree value) {
    Path<?> path = entity.get(name);
    ParameterExpression<String> parameter = createParameter(name, value, literal -> "%%" + literal);
    Predicate predicate = criteriaBuilder.like(path.as(String.class), parameter, '\\');
    predicates.add(predicate);
  }

  public void like(String name, ParseTree value) {
    Path<?> path = entity.get(name);
    ParameterExpression<String> parameter = createParameter(
        name, value, literal -> "%%" + literal + "%%");
    Predicate predicate = criteriaBuilder.like(path.as(String.class), parameter, '\\');
    predicates.add(predicate);
  }

  public void order(String name, Token direction) {
    orders.add(getTokenName(direction).equals("ASC")
        ? criteriaBuilder.asc(entity.get(name))
        : criteriaBuilder.desc(entity.get(name)));
  }

  public void offset(int offset) {
    result.offset = offset;
  }

  public void limit(int limit) {
    result.limit = limit;
  }

  @SuppressWarnings("unchecked")
  public void end() {
    Predicate predicate = mergePredicates(0);
    if (predicate != null) {
      criteriaQuery.where(predicate);
    }

    criteriaQuery.multiselect(criteriaBuilder.count(entity));
    result.countQuery = em.createQuery(criteriaQuery);

    // orders will be overwritten by SearchEngine#search with pageable
    criteriaQuery.multiselect(entity).orderBy(orders);
    result.query = em.createQuery(criteriaQuery);

    for (Map.Entry<Parameter<?>, String> entry : parameters.entrySet()) {
      Parameter<Object> parameter = (Parameter<Object>) entry.getKey();
      result.query.setParameter(parameter, entry.getValue());
      result.countQuery.setParameter(parameter, entry.getValue());
    }

    if (result.offset != null) {
      if (result.offset < 0) {
        throw new IllegalParameterException("offset must be non-negative");
      }
      result.query.setFirstResult(result.offset);
    }
    if (result.limit != null) {
      if (result.limit <= 0) {
        throw new IllegalParameterException("limit must be bigger than 0");
      }
      result.query.setMaxResults(result.limit);
    }
  }

  @SuppressWarnings("unchecked")
  public ResultImpl build() {
    if (result.query == null) {
      throw new IllegalStateException();
    }
    return result;
  }

  private Predicate mergePredicates(int startFrom) {
    if (predicates.isEmpty()) {
      return null;
    }
    Predicate predicate = predicates.get(startFrom);
    for (int i = startFrom + 1; i < predicates.size(); i++) {
      if (getTokenName(operations.get(i - 1)).equals("LOGICAL_AND")) {
        predicate = criteriaBuilder.and(predicate, predicates.get(i));
      } else {
        predicate = criteriaBuilder.or(predicate, predicates.get(i));
      }
    }
    predicates = predicates.subList(0, startFrom);
    operations = operations.subList(0, startFrom);
    predicates.add(predicate);
    return predicate;
  }

  private <T> T createPredicate(
      String name, ParseTree value,
      BiFunction<Path<? extends Comparable<? super Object>>,
          ParameterExpression<? extends Comparable<? super Object>>, T> action) {
    Path<? extends Comparable<? super Object>> path = entity.get(name);
    ParameterExpression<? extends Comparable<? super Object>> parameter =
        createParameter(name, value);
    return action.apply(path, parameter);
  }

  private <T extends Comparable<? super T>> ParameterExpression<T> createParameter(
      String name, ParseTree value) {
    return createParameter(name, value, Function.identity());
  }

  @SuppressWarnings("unchecked")
  private <T extends Comparable<? super T>> ParameterExpression<T> createParameter(
      String name, ParseTree value, Function<String, String> valueModifier) {
    boolean interpretedStringLit = getTokenName(((TerminalNode) value).getSymbol())
        .equals("INTERPRETED_STRING_LIT");
    String literal = interpretedStringLit
        ? Utils.unwrap(value.getText(), '"')
        : value.getText();

    Class<Object> type = entity.get(name).getModel().getBindableJavaType();
    ParameterExpression<?> parameter = criteriaBuilder.parameter(type);
    parameters.put(parameter, valueModifier.apply(literal));

    return (ParameterExpression<T>) parameter;
  }

  private boolean isLiteral(String name) {
    return String.class.isAssignableFrom(entity.get(name).getModel().getBindableJavaType());
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ResultImpl implements FiltersQueryBuilder.Result {

    private TypedQuery<Tuple> query;
    private TypedQuery<Tuple> countQuery;
    private Integer offset;
    private Integer limit;
  }
}
