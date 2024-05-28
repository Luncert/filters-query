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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.luncert.filtersquery.api.BasicFiltersQueryBuilder;
import org.luncert.filtersquery.api.FiltersQueryBuilder;
import org.luncert.filtersquery.api.exception.IllegalParameterException;
import org.luncert.filtersquery.api.grammar.FiltersQueryParser;

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
  private final Map<Parameter<?>, Object> parameters = new HashMap<>();
  private final ResultImpl result = new ResultImpl();

  private final Map<String, Root<?>> aliases = new HashMap<>();
  private final Stack<Integer> conjunctionParenStack = new Stack<>();
  private List<Predicate> conjunctionPredicates = new ArrayList<>();
  private List<Integer> conjunctionOperations = new LinkedList<>();

  @Override
  public void defineAlias(Map<String, String> aliasMap) {
    aliasMap.forEach((entityName, aliasName) -> {
      var entityClassName = entity.getJavaType().getPackage().getName() + "." + entityName;
      try {
        this.aliases.put(aliasName, criteriaQuery.from(Class.forName(entityClassName)));
      } catch (ClassNotFoundException e) {
        throw new RuntimeException("cannot resolve entity with name " + entityName);
      }
    });
  }

  @Override
  public void conjunctionEqual(String left, String right) {
    conjunctionPredicates.add(createPredicate(left, right, criteriaBuilder::equal));
  }

  @Override
  public void conjunctionNotEqual(String left, String right) {
    conjunctionPredicates.add(createPredicate(left, right, criteriaBuilder::notEqual));
  }

  @Override
  public void enterConjunctionParentheses() {
    conjunctionParenStack.push(conjunctionPredicates.size());
  }

  @Override
  public void exitConjunctionParentheses() {
    int startFrom = conjunctionParenStack.pop();
    // construct predicate
    mergeConjunctionPredicates(startFrom);
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
    if (inConjunction) {
      conjunctionOperations.add(operator.getType());
    } else {
      operations.add(operator.getType());
    }
  }

  @Override
  public void equal(String name, FiltersQueryParser.PropertyValueWithReferenceBoolNullContext value) {
    if (isNull(value)) {
      Path<? extends Comparable<? super Object>> path = entity.get(name);
      predicates.add(criteriaBuilder.isNull(path));
      return;
    }

    predicates.add(createPredicate(name, value, criteriaBuilder::equal));
  }

  /**
   * Assert column not equal to specified value, not null and not empty.
   */
  @Override
  public void notEqual(String name, FiltersQueryParser.PropertyValueWithReferenceBoolNullContext value) {
    if (isNull(value)) {
      Path<? extends Comparable<? super Object>> path = entity.get(name);
      predicates.add(criteriaBuilder.isNotNull(path));
      return;
    }

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
  @Override
  public void empty(String name) {
    Path<?> path = entity.get(name);
    Predicate predicate = criteriaBuilder.isNull(path);
    if (isLiteral(name)) {
      predicate = criteriaBuilder.or(predicate,
          criteriaBuilder.equal(criteriaBuilder.length(path.as(String.class)), 0));
    }
    predicates.add(predicate);
  }

  @Override
  public void notEmpty(String name) {
    Path<?> path = entity.get(name);
    Predicate predicate = criteriaBuilder.isNotNull(path);
    if (isLiteral(name)) {
      predicate = criteriaBuilder.and(predicate,
          criteriaBuilder.greaterThan(criteriaBuilder.length(path.as(String.class)), 0));
    }
    predicates.add(predicate);
  }

  @Override
  public void in(String name, List<FiltersQueryParser.PropertyValueWithReferenceBoolNullContext> values) {
    Path<? extends Comparable<? super Object>> path = entity.get(name);

    AtomicBoolean containsNull = new AtomicBoolean(false);
    var parsedValues = values.stream().filter(v -> {
      if (isNull(v)) {
        containsNull.set(true);
        return false;
      }
      return true;
    }).map(this::getLiteral).toList();
    var predicate = path.in(parsedValues);

    if (containsNull.get()) {
      predicates.add(criteriaBuilder.or(predicate, criteriaBuilder.isNull(path)));
    } else {
      predicates.add(predicate);
    }
  }

  @Override
  public void greaterThanEqual(String name, FiltersQueryParser.PropertyValueWithReferenceContext value) {
    predicates.add(createPredicate(name, value, criteriaBuilder::greaterThanOrEqualTo));
  }

  @Override
  public void greaterThan(
      String name, FiltersQueryParser.PropertyValueWithReferenceContext value) {
    predicates.add(createPredicate(name, value, criteriaBuilder::greaterThan));
  }

  @Override
  public void lessThanEqual(
      String name, FiltersQueryParser.PropertyValueWithReferenceContext value) {
    predicates.add(createPredicate(name, value, criteriaBuilder::lessThanOrEqualTo));
  }

  @Override
  public void lessThan(
      String name, FiltersQueryParser.PropertyValueWithReferenceContext value) {
    predicates.add(createPredicate(name, value, criteriaBuilder::lessThan));
  }

  @Override
  public void between(
      String name, FiltersQueryParser.PropertyValueContext startValue, FiltersQueryParser.PropertyValueContext endValue) {
    Path<? extends Comparable<? super Object>> path = entity.get(name);
    ParameterExpression<? extends Comparable<? super Object>> p1 =
        createParameter(name, startValue);
    ParameterExpression<? extends Comparable<? super Object>> p2 = createParameter(name, endValue);
    Predicate predicate = criteriaBuilder.between(path, p1, p2);
    predicates.add(predicate);
  }

  @Override
  public void startsWith(
      String name, FiltersQueryParser.StringPropertyValueContext value) {
    Path<?> path = entity.get(name);
    ParameterExpression<String> parameter = createParameter(name, value, literal -> literal + "%%");
    Predicate predicate = criteriaBuilder.like(path.as(String.class), parameter, '\\');
    predicates.add(predicate);
  }

  @Override
  public void endsWith(
      String name, FiltersQueryParser.StringPropertyValueContext value) {
    Path<?> path = entity.get(name);
    ParameterExpression<String> parameter = createParameter(name, value, literal -> "%%" + literal);
    Predicate predicate = criteriaBuilder.like(path.as(String.class), parameter, '\\');
    predicates.add(predicate);
  }

  @Override
  public void like(String name, FiltersQueryParser.StringPropertyValueContext value) {
    Path<?> path = entity.get(name);
    ParameterExpression<String> parameter = createParameter(
        name, value, literal -> "%%" + literal + "%%");
    Predicate predicate = criteriaBuilder.like(path.as(String.class), parameter, '\\');
    predicates.add(predicate);
  }

  @Override
  public void order(String name, Token direction) {
    orders.add(getTokenName(direction).equals("ASC")
        ? criteriaBuilder.asc(entity.get(name))
        : criteriaBuilder.desc(entity.get(name)));
  }

  @Override
  public void offset(int offset) {
    result.offset = offset;
  }

  @Override
  public void limit(int limit) {
    result.limit = limit;
  }

  @SuppressWarnings("unchecked")
  public void end() {
    Predicate predicate = mergePredicates(0);
    // entity.join(entity.get("12"), JoinType.LEFT);
    Predicate conjunctionPredicate = mergeConjunctionPredicates(0);
    if (predicate != null) {
      if (conjunctionPredicate != null) {
        predicate = criteriaBuilder.and(predicate, conjunctionPredicate);
      }
      criteriaQuery.where(predicate);
    } else if (conjunctionPredicate != null) {
      criteriaQuery.where(conjunctionPredicate);
    }

    criteriaQuery.multiselect(criteriaBuilder.count(entity));
    result.countQuery = em.createQuery(criteriaQuery);

    // orders will be overwritten by SearchEngine#search with pageable
    criteriaQuery.multiselect(entity).orderBy(orders);
    result.query = em.createQuery(criteriaQuery);

    for (Map.Entry<Parameter<?>, Object> entry : parameters.entrySet()) {
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

  private Predicate mergeConjunctionPredicates(int startFrom) {
    if (predicates.isEmpty()) {
      return null;
    }
    Predicate predicate = conjunctionPredicates.get(startFrom);
    for (int i = startFrom + 1; i < conjunctionPredicates.size(); i++) {
      if (getTokenName(conjunctionOperations.get(i - 1)).equals("LOGICAL_AND")) {
        predicate = criteriaBuilder.and(predicate, predicates.get(i));
      } else {
        predicate = criteriaBuilder.or(predicate, predicates.get(i));
      }
    }
    conjunctionPredicates = conjunctionPredicates.subList(0, startFrom);
    conjunctionOperations = conjunctionOperations.subList(0, startFrom);
    conjunctionPredicates.add(predicate);
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

  private <T> T createPredicate(
      String left, String right,
      BiFunction<Path<? extends Comparable<? super Object>>,
          Path<? extends Comparable<? super Object>>, T> action) {
    var refLeft = Reference.of(left);
    var refRight = Reference.of(right);

    Path<? extends Comparable<? super Object>> vl =
        aliases.get(refLeft.entityAlias).get(refLeft.field);
    Path<? extends Comparable<? super Object>> vr =
        aliases.get(refRight.entityAlias).get(refRight.field);

    return action.apply(vl, vr);
  }

  private <T extends Comparable<? super T>> ParameterExpression<T> createParameter(
      String name, ParseTree value) {
    return createParameter(name, value, Function.identity());
  }

  @SuppressWarnings("unchecked")
  private <T extends Comparable<? super T>> ParameterExpression<T> createParameter(
      String name, ParseTree value, Function<String, String> valueModifier) {
    String literal = getLiteral(value);

    Class<Object> type = entity.get(name).getModel().getBindableJavaType();
    ParameterExpression<?> parameter = criteriaBuilder.parameter(type);

    if (Boolean.class.isAssignableFrom(type)) {
      parameters.put(parameter, Boolean.valueOf(literal));
    } else {
      parameters.put(parameter, valueModifier.apply(literal));
    }

    return (ParameterExpression<T>) parameter;
  }

  private boolean isNull(ParseTree value) {
    if (value instanceof FiltersQueryParser.PropertyValueWithReferenceBoolNullContext ctx) {
      return ctx.NULL() != null;
    }

    return false;
  }

  private boolean isLiteral(String name) {
    return String.class.isAssignableFrom(entity.get(name).getModel().getBindableJavaType());
  }

  @AllArgsConstructor
  private static class Reference {
    private String entityAlias;
    private String field;

    static Reference of(String raw) {
      var patterns = raw.split("\\.");
      assert patterns.length == 2;
      return new Reference(patterns[0], patterns[1]);
    }
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
