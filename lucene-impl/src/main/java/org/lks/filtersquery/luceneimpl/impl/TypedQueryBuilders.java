package org.lks.filtersquery.luceneimpl.impl;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TypedQueryBuilders {

  private static final DoubleQueryBuilder doubleQueryBuilder = new DoubleQueryBuilder();
  private static final FloatQueryBuilder floatQueryBuilder = new FloatQueryBuilder();
  private static final LongQueryBuilder longQueryBuilder = new LongQueryBuilder();
  private static final IntQueryBuilder intQueryBuilder = new IntQueryBuilder();
  private static final StringQueryBuilder stringQueryBuilder = new StringQueryBuilder();

  private static final Map<Class<?>, TypedQueryBuilder<?>> typeMetadataMap =
      ImmutableMap.<Class<?>, TypedQueryBuilder<?>>builder()
          .put(Double.class, doubleQueryBuilder)
          .put(double.class, doubleQueryBuilder)
          .put(Float.class, floatQueryBuilder)
          .put(float.class, floatQueryBuilder)
          .put(Long.class, longQueryBuilder)
          .put(long.class, longQueryBuilder)
          .put(Integer.class, intQueryBuilder)
          .put(int.class, intQueryBuilder)
          .put(String.class, stringQueryBuilder)
          .build();

  @SuppressWarnings("unchecked")
  public static <T> TypedQueryBuilder<T> get(Class<T> type) {
    TypedQueryBuilder<T> typeMetadata = (TypedQueryBuilder<T>) typeMetadataMap.get(type);
    if (typeMetadata == null) {
      throw new IllegalArgumentException("unsupported field type " + type);
    }
    return typeMetadata;
  }
}
