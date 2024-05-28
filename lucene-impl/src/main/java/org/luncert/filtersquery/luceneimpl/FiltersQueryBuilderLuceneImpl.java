package org.luncert.filtersquery.luceneimpl;

import static org.apache.lucene.search.BooleanClause.Occur.MUST;
import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortedNumericSortField;
import org.luncert.filtersquery.api.BasicFiltersQueryBuilder;
import org.luncert.filtersquery.api.FiltersQueryBuilder;
import org.luncert.filtersquery.api.grammar.FiltersQueryParser;
import org.luncert.filtersquery.luceneimpl.builder.TypedQueryBuilder;
import org.luncert.filtersquery.luceneimpl.builder.TypedQueryBuilders;
import org.luncert.filtersquery.luceneimpl.exception.FiltersQueryLucenceBuilderException;

@RequiredArgsConstructor
public class FiltersQueryBuilderLuceneImpl extends BasicFiltersQueryBuilder {

  private final Stack<Integer> parenStack = new Stack<>();
  private List<Query> queries = new LinkedList<>();
  private List<Integer> operations = new LinkedList<>();
  private final List<SortField> sorts = new ArrayList<>();
  private final ResultImpl result = new ResultImpl();
  private final Class<?> entityType;

  @Override
  public void enterParentheses() {
    parenStack.push(queries.size());
  }

  @Override
  public void exitParentheses() {
    int startFrom = parenStack.pop();
    mergeQueries(startFrom);
  }

  @Override
  public void operator(Token operator) {
    operations.add(operator.getType());
  }

  @Override
  public void equal(String name, FiltersQueryParser.PropertyValueWithReferenceBoolNullContext value) {
    queries.add(getTypeMetadata(name).equal(name, getLiteral(value)));
  }

  @Override
  public void notEqual(String name, FiltersQueryParser.PropertyValueWithReferenceBoolNullContext value) {
    queries.add(getTypeMetadata(name).notEqual(name, getLiteral(value)));
  }

  @Override
  public void empty(String name) {
    queries.add(getTypeMetadata(name).empty(name));
  }

  @Override
  public void notEmpty(String name) {
    queries.add(getTypeMetadata(name).notEmpty(name));
  }

  @Override
  public void in(String name, List<FiltersQueryParser.PropertyValueWithReferenceBoolNullContext> values) {
    queries.add(getTypeMetadata(name).in(name, values.stream().map(this::getLiteral).toList()));
  }

  @Override
  public void greaterThanEqual(String name, FiltersQueryParser.PropertyValueWithReferenceContext value) {
    queries.add(getTypeMetadata(name).greaterEqualThan(name, getLiteral(value)));
  }

  @Override
  public void greaterThan(String name, FiltersQueryParser.PropertyValueWithReferenceContext value) {
    queries.add(getTypeMetadata(name).greaterThan(name, getLiteral(value)));
  }

  @Override
  public void lessThanEqual(String name, FiltersQueryParser.PropertyValueWithReferenceContext value) {
    queries.add(getTypeMetadata(name).lessEqualThan(name, getLiteral(value)));
  }

  @Override
  public void lessThan(String name, FiltersQueryParser.PropertyValueWithReferenceContext value) {
    queries.add(getTypeMetadata(name).lessThan(name, getLiteral(value)));
  }

  @Override
  public void between(String name, FiltersQueryParser.PropertyValueContext startValue, FiltersQueryParser.PropertyValueContext endValue) {
    queries.add(getTypeMetadata(name).between(name, getLiteral(startValue), getLiteral(endValue)));
  }

  @Override
  public void startsWith(String name, FiltersQueryParser.StringPropertyValueContext value) {
    queries.add(getTypeMetadata(name).startsWith(name, getLiteral(value)));
  }

  @Override
  public void endsWith(String name, FiltersQueryParser.StringPropertyValueContext value) {
    queries.add(getTypeMetadata(name).endsWith(name, getLiteral(value)));
  }

  @Override
  public void like(String name, FiltersQueryParser.StringPropertyValueContext value) {
    queries.add(getTypeMetadata(name).like(name, getLiteral(value)));
  }

  @Override
  public void order(String name, Token direction) {
    sorts.add(new SortedNumericSortField(name, getTypeMetadata(name).getSortType(),
        getTokenName(direction).equals("DESC")));
  }

  @Override
  public void offset(int offset) {
    result.offset = offset;
  }

  @Override
  public void limit(int limit) {
    result.limit = limit;
  }

  @Override
  public void end() {
    result.query = mergeQueries(0);
    if (result.query == null) {
      result.query = new MatchAllDocsQuery();
    }
    if (!sorts.isEmpty()) {
      result.sort = new Sort(sorts.toArray(new SortField[0]));
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public ResultImpl build() {
    return result;
  }

  private Query mergeQueries(int startFrom) {
    if (queries.isEmpty()) {
      return null;
    }

    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    int i = startFrom;
    for (; i < queries.size() - 1; i++) {
      if (getTokenName(operations.get(i)).equals("LOGICAL_AND")) {
        builder.add(new BooleanClause(queries.get(i), MUST));
      } else {
        builder.add(new BooleanClause(queries.get(i), SHOULD));
      }
    }

    Query query;
    if (operations.isEmpty()) {
      query = queries.get(i);
    } else {
      if (getTokenName(operations.get(i - 1)).equals("LOGICAL_AND")) {
        builder.add(new BooleanClause(queries.get(i), MUST));
      } else {
        builder.add(new BooleanClause(queries.get(i), SHOULD));
      }
      query = builder.build();
      queries = queries.subList(0, startFrom);
      operations = operations.subList(0, startFrom);
      queries.add(query);
    }

    return query;
  }

  @SuppressWarnings("unchecked")
  private <T> TypedQueryBuilder<T> getTypeMetadata(String name) {
    try {
      return TypedQueryBuilders.get((Class<T>) entityType.getDeclaredField(name).getType());
    } catch (NoSuchFieldException e) {
      throw new FiltersQueryLucenceBuilderException(e);
    }
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ResultImpl implements FiltersQueryBuilder.Result {

    @Nullable
    private Query query;
    private Sort sort;
    private Integer offset;
    private Integer limit;
  }
}
