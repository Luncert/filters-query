package org.lks.filtersquery.luceneimpl;

import static org.apache.lucene.search.BooleanClause.Occur.MUST;
import static org.apache.lucene.search.BooleanClause.Occur.MUST_NOT;
import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.BytesRef;
import org.lks.filtersquery.api.BasicFiltersQueryBuilder;
import org.lks.filtersquery.api.FiltersQueryBuilder;
import org.lks.filtersquery.api.Utils;

public class FiltersQueryBuilderLuceneImpl extends BasicFiltersQueryBuilder {

  private final Stack<Integer> parenStack = new Stack<>();
  private List<Query> queries = new LinkedList<>();
  private List<Integer> operations = new LinkedList<>();
  private final List<SortField> sorts = new ArrayList<>();
  private final ResultImpl result = new ResultImpl();

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
    queries.add(new TermQuery(new Term(name, getLiteral(value))));
  }

  @Override
  public void notEqual(String name, ParseTree value) {
    queries.add(new BooleanQuery.Builder()
        .add(new BooleanClause(new TermQuery(new Term(name, getLiteral(value))), MUST_NOT))
        .build());
  }

  @Override
  public void empty(String name) {
    // TODO:
  }

  @Override
  public void notEmpty(String name) {
    // TODO:
  }

  @Override
  public void greaterEqualThan(String name, ParseTree value) {
    queries.add(new TermRangeQuery(name,
        new BytesRef(getLiteral(value).getBytes(StandardCharsets.UTF_8)),
        new BytesRef("*".getBytes(StandardCharsets.UTF_8)),
        true,
        false
    ));
  }

  @Override
  public void greaterThan(String name, ParseTree value) {
    queries.add(new TermRangeQuery(name,
        new BytesRef(getLiteral(value).getBytes(StandardCharsets.UTF_8)),
        new BytesRef("*".getBytes(StandardCharsets.UTF_8)),
        false,
        false
    ));
  }

  @Override
  public void lessEqualThan(String name, ParseTree value) {
    queries.add(new TermRangeQuery(name,
        new BytesRef("*".getBytes(StandardCharsets.UTF_8)),
        new BytesRef(getLiteral(value).getBytes(StandardCharsets.UTF_8)),
        true,
        false
    ));
  }

  @Override
  public void lessThan(String name, ParseTree value) {
    queries.add(new TermRangeQuery(name,
        new BytesRef("*".getBytes(StandardCharsets.UTF_8)),
        new BytesRef(getLiteral(value).getBytes(StandardCharsets.UTF_8)),
        false,
        false
    ));
  }

  @Override
  public void between(String name, ParseTree startValue, ParseTree endValue) {
    queries.add(new TermRangeQuery(name,
        new BytesRef(getLiteral(startValue).getBytes(StandardCharsets.UTF_8)),
        new BytesRef(getLiteral(endValue).getBytes(StandardCharsets.UTF_8)),
        true,
        true
    ));
  }

  @Override
  public void startsWith(String name, ParseTree value) {
    queries.add(new WildcardQuery(new Term(name, getLiteral(value) + "*")));
  }

  @Override
  public void endsWith(String name, ParseTree value) {
    queries.add(new WildcardQuery(new Term(name, "*" + getLiteral(value))));
  }

  @Override
  public void like(String name, ParseTree value) {
    queries.add(new FuzzyQuery(new Term(name, getLiteral(value) + "~")));
  }

  @Override
  public void order(String name, Token direction) {
    sorts.add(new SortField(name, SortField.Type.STRING));
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
    if (getTokenName(operations.get(i - 1)).equals("LOGICAL_AND")) {
      builder.add(new BooleanClause(queries.get(i), MUST));
    } else {
      builder.add(new BooleanClause(queries.get(i), SHOULD));
    }
    queries = queries.subList(0, startFrom);
    operations = operations.subList(0, startFrom);

    Query query = builder.build();
    queries.add(query);
    return query;
  }

  private String getLiteral(ParseTree value) {
    boolean interpretedStringLit = getTokenName(((TerminalNode) value).getSymbol())
        .equals("INTERPRETED_STRING_LIT");
    return interpretedStringLit
        ? Utils.unwrap(value.getText(), '"')
        : value.getText();
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ResultImpl implements FiltersQueryBuilder.Result {

    private Query query;
    private Integer offset;
    private Integer limit;
  }
}
