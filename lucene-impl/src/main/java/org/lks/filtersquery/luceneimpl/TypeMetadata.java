package org.lks.filtersquery.luceneimpl;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.BiFunction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;

@Getter
@AllArgsConstructor
enum TypeMetadata {
  DOUBLE(
      SortField.Type.DOUBLE,
      (name, literalValue) -> DoublePoint.newExactQuery(name, Double.parseDouble(literalValue)),
      (name, literalValue) -> new BooleanQuery.Builder()
          .add(DoublePoint.newRangeQuery(name, Double.MIN_VALUE, Double.MAX_VALUE), BooleanClause.Occur.SHOULD)
          .add(DoublePoint.newExactQuery(name, Double.parseDouble(literalValue)), BooleanClause.Occur.MUST_NOT)
          .build()
  ),
  FLOAT(
      SortField.Type.FLOAT,
      (name, literalValue) -> FloatPoint.newExactQuery(name, Float.parseFloat(literalValue)),
      (name, literalValue) -> new BooleanQuery.Builder()
          .add(FloatPoint.newRangeQuery(name, Float.MIN_VALUE, Float.MAX_VALUE), BooleanClause.Occur.SHOULD)
          .add(FloatPoint.newExactQuery(name, Float.parseFloat(literalValue)), BooleanClause.Occur.MUST_NOT)
          .build()
  ),
  LONG(
      SortField.Type.LONG,
      (name, literalValue) -> LongPoint.newExactQuery(name, Long.parseLong(literalValue)),
      (name, literalValue) -> new BooleanQuery.Builder()
          .add(LongPoint.newRangeQuery(name, Long.MIN_VALUE, Long.MAX_VALUE), BooleanClause.Occur.SHOULD)
          .add(LongPoint.newExactQuery(name, Long.parseLong(literalValue)), BooleanClause.Occur.MUST_NOT)
          .build()
  ),
  INT(
      SortField.Type.INT,
      (name, literalValue) -> IntPoint.newExactQuery(name, Integer.parseInt(literalValue)),
      (name, literalValue) -> new BooleanQuery.Builder()
          .add(IntPoint.newRangeQuery(name, Integer.MIN_VALUE, Integer.MAX_VALUE), BooleanClause.Occur.SHOULD)
          .add(IntPoint.newExactQuery(name, Integer.parseInt(literalValue)), BooleanClause.Occur.MUST_NOT)
          .build()
  ),
  STRING(
      SortField.Type.STRING,
      (name, literalValue) -> new TermQuery(new Term(name, literalValue)),
      (name, literalValue) -> new BooleanQuery.Builder()
          .add(LongPoint.newRangeQuery(name, Long.MIN_VALUE, Long.MAX_VALUE), BooleanClause.Occur.SHOULD)
          .add(LongPoint.newExactQuery(name, Long.parseLong(literalValue)), BooleanClause.Occur.MUST_NOT)
          .build()
  );

  private final SortField.Type sortType;
  private final BiFunction<String, String, Query> exactQueryBuilder;
  private final BiFunction<String, String, Query> exactNotQueryBuilder;

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

  public static TypeMetadata get(Class<?> type) {
    TypeMetadata typeMetadata = typeMetadataMap.get(type);
    if (typeMetadata == null) {
      throw new IllegalArgumentException("unsupported field type " + type);
    }
    return typeMetadata;
  }
}

