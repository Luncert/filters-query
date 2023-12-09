package org.luncert.filtersquery.predicateimpl.builder;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TypedPredicateBuilders {

  private static final DoublePredicateBuilder doublePredicateBuilder = new DoublePredicateBuilder();
  private static final FloatPredicateBuilder floatPredicateBuilder = new FloatPredicateBuilder();
  private static final LongPredicateBuilder longPredicateBuilder = new LongPredicateBuilder();
  private static final IntPredicateBuilder intPredicateBuilder = new IntPredicateBuilder();
  private static final StringPredicateBuilder stringPredicateBuilder = new StringPredicateBuilder();

  private static final Map<Class<?>, TypedPredicateBuilder<?>> typeMetadataMap =
      ImmutableMap.<Class<?>, TypedPredicateBuilder<?>>builder()
          .put(Double.class, doublePredicateBuilder)
          .put(double.class, doublePredicateBuilder)
          .put(Float.class, floatPredicateBuilder)
          .put(float.class, floatPredicateBuilder)
          .put(Long.class, longPredicateBuilder)
          .put(long.class, longPredicateBuilder)
          .put(Integer.class, intPredicateBuilder)
          .put(int.class, intPredicateBuilder)
          .put(String.class, stringPredicateBuilder)
          .build();

  public static TypedPredicateBuilder<?> get(Class<?> type) {
    TypedPredicateBuilder<?> typeMetadata = typeMetadataMap.get(type);
    if (typeMetadata == null) {
      throw new IllegalArgumentException("unsupported field type " + type);
    }
    return typeMetadata;
  }
}
