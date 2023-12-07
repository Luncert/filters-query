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
import java.util.function.BiFunction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.BytesRef;
import org.lks.filtersquery.api.BasicFiltersQueryBuilder;
import org.lks.filtersquery.api.FiltersQueryBuilder;
import org.lks.filtersquery.api.Utils;

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
    queries.add(getTypeMetadata(name).exactQueryBuilder.apply(name, getLiteral(value)));
  }

  @Override
  public void notEqual(String name, ParseTree value) {
    queries.add(new BooleanQuery.Builder()
        .add(new BooleanClause(new WildcardQuery(new Term(name, "*")), SHOULD))
        .add(new BooleanClause(getTypeMetadata(name).exactQueryBuilder
            .apply(name, getLiteral(value)), MUST_NOT))
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
    queries.add(new RegexpQuery(new Term(name, "^" + getLiteral(value) + ".*")));
  }

  @Override
  public void endsWith(String name, ParseTree value) {
    queries.add(new RegexpQuery(new Term(name, ".*" + getLiteral(value) + "^")));
  }

  @Override
  public void like(String name, ParseTree value) {
    queries.add(new TermQuery(new Term(name, getLiteral(value))));
  }

  @Override
  public void order(String name, Token direction) {
    sorts.add(new SortField(name, getTypeMetadata(name).sortType));
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

  @AllArgsConstructor
  enum TypeMetadata {
    DOUBLE(SortField.Type.DOUBLE,
        (name, literalValue) -> DoublePoint.newExactQuery(name, Double.parseDouble(literalValue))),
    FLOAT(SortField.Type.FLOAT,
        (name, literalValue) -> FloatPoint.newExactQuery(name, Float.parseFloat(literalValue))),
    LONG(SortField.Type.LONG,
        (name, literalValue) -> LongPoint.newExactQuery(name, Long.parseLong(literalValue))),
    INT(SortField.Type.INT,
        (name, literalValue) -> IntPoint.newExactQuery(name, Integer.parseInt(literalValue))),
    STRING(SortField.Type.STRING,
        (name, literalValue) -> new TermQuery(new Term(name, literalValue)));

    private final SortField.Type sortType;
    private final BiFunction<String, String, Query> exactQueryBuilder;
  }

  private static final Map<Class<?>, TypeMetadata> typeMetadataMap =
      ImmutableMap.<Class<?>, TypeMetadata>builder()
          .put(Double.class, TypeMetadata.DOUBLE)
          .put(double.class, TypeMetadata.DOUBLE)
          .put(Float.class, TypeMetadata.FLOAT)
          .put(float.class, TypeMetadata.FLOAT)
          .put(Long.class, TypeMetadata.LONG)
          .put(long.class, TypeMetadata.LONG)
          .put(Integer.class, TypeMetadata.INT)
          .put(int.class, TypeMetadata.INT)
          .put(String.class, TypeMetadata.STRING)
          .build();

  private TypeMetadata getTypeMetadata(String name) {
    try {
      Class<?> type = entityType.getDeclaredField(name).getType();
      TypeMetadata typeMetadata = FiltersQueryBuilderLuceneImpl.typeMetadataMap.get(type);
      if (typeMetadata == null) {
        throw new IllegalArgumentException("unsupported field type " + type);
      }
      return typeMetadata;
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
