package org.lks.filtersquery.luceneimpl;

import static org.apache.lucene.search.BooleanClause.Occur.MUST;
import static org.apache.lucene.search.BooleanClause.Occur.MUST_NOT;
import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;

import com.google.common.collect.ImmutableMap;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.BytesRef;
import org.lks.filtersquery.api.BasicFiltersQueryBuilder;
import org.lks.filtersquery.api.FiltersQueryBuilder;
import org.lks.filtersquery.api.Utils;
import org.lks.filtersquery.luceneimpl.impl.TypedQueryBuilder;
import org.lks.filtersquery.luceneimpl.impl.TypedQueryBuilders;

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
  public void equal(String name, ParseTree value) {
    queries.add(getTypeMetadata(name).equal(name, getLiteral(value)));
  }

  @Override
  public void notEqual(String name, ParseTree value) {
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
  public void greaterEqualThan(String name, ParseTree value) {
    queries.add(getTypeMetadata(name).greaterEqualThan(name, getLiteral(value)));
  }

  @Override
  public void greaterThan(String name, ParseTree value) {
    queries.add(getTypeMetadata(name).greaterThan(name, getLiteral(value)));
  }

  @Override
  public void lessEqualThan(String name, ParseTree value) {
    queries.add(getTypeMetadata(name).lessEqualThan(name, getLiteral(value)));
  }

  @Override
  public void lessThan(String name, ParseTree value) {
    queries.add(getTypeMetadata(name).lessThan(name, getLiteral(value)));
  }

  @Override
  public void between(String name, ParseTree startValue, ParseTree endValue) {
    queries.add(getTypeMetadata(name).between(name, getLiteral(startValue), getLiteral(endValue)));
  }

  @Override
  public void startsWith(String name, ParseTree value) {
    queries.add(getTypeMetadata(name).startsWith(name, getLiteral(value)));
  }

  @Override
  public void endsWith(String name, ParseTree value) {
    queries.add(getTypeMetadata(name).endsWith(name, getLiteral(value)));
  }

  @Override
  public void like(String name, ParseTree value) {
    queries.add(getTypeMetadata(name).like(name, getLiteral(value)));
  }

  @Override
  public void order(String name, Token direction) {
    sorts.add(new SortField(name, getTypeMetadata(name).getSortType()));
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
    if (!sorts.isEmpty()) {
      result.sort = new Sort(sorts.toArray(new SortField[0]));
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public ResultImpl build() {
    if (result.query == null) {
      throw new IllegalStateException();
    }
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

  private String getLiteral(ParseTree value) {
    return isStringLiteral(value)
        ? Utils.unwrap(value.getText(), '"')
        : value.getText();
  }

  private boolean isStringLiteral(ParseTree value) {
    return getTokenName(((TerminalNode) value).getSymbol())
        .equals("INTERPRETED_STRING_LIT");
  }

  @SuppressWarnings("unchecked")
  private <T> TypedQueryBuilder<T> getTypeMetadata(String name) {
    try {
      return TypedQueryBuilders.get((Class<T>) entityType.getDeclaredField(name).getType());
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ResultImpl implements FiltersQueryBuilder.Result {

    private Query query;
    private Sort sort;
    private Integer offset;
    private Integer limit;
  }
}
