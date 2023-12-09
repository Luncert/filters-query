package org.luncert.filtersquery.luceneimpl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.luncert.filtersquery.luceneimpl.builder.TypedQueryBuilders;

public class LuceneFiltersQueryOrmEngine<E> extends LuceneFiltersQueryEngine {

  private final Class<E> entityType;

  @SuppressWarnings("unchecked")
  public LuceneFiltersQueryOrmEngine() {
    this.entityType = (Class<E>) ((ParameterizedType) getClass()
        .getGenericSuperclass()).getActualTypeArguments()[0];
  }

  protected long count(IndexReader indexReader, String criteria) throws IOException {
    FiltersQueryBuilderLuceneImpl.ResultImpl result = buildQuery(criteria, entityType);
    IndexSearcher searcher = new IndexSearcher(indexReader);
    TopDocs topDocs = searcher.search(result.getQuery(), Integer.MAX_VALUE);
    return topDocs.totalHits;
  }

  public List<E> search(IndexReader indexReader, String criteria) throws IOException {
    return search(indexReader, criteria, this::toEntities);
  }

  public <R> R search(IndexReader indexReader,
                      String criteria,
                      Function<Stream<Document>, R> resultMapper) throws IOException {
    FiltersQueryBuilderLuceneImpl.ResultImpl result = buildQuery(criteria, entityType);

    int offset = result.getOffset() == null ? 0 : result.getOffset();
    int limit = result.getLimit() == null ? Integer.MAX_VALUE : result.getLimit();
    int total = Math.min(offset + limit, Integer.MAX_VALUE);

    IndexSearcher searcher = new IndexSearcher(indexReader);
    TopDocs topDocs = result.getSort() == null
        ? searcher.search(result.getQuery(), total)
        : searcher.search(result.getQuery(), total, result.getSort());
    return resultMapper.apply(
        Arrays.stream(topDocs.scoreDocs, offset, Math.min(topDocs.scoreDocs.length, total))
            .map(scoreDoc -> {
              try {
                return indexReader.document(scoreDoc.doc);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            }));
  }

  private List<E> toEntities(Stream<Document> documentStream) {
    return documentStream.map(doc -> {
      E obj;

      try {
        obj = entityType.getDeclaredConstructor().newInstance();
      } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
               IllegalAccessException e) {
        throw new RuntimeException("failed to initialize " + entityType, e);
      }

      for (Field field : entityType.getDeclaredFields()) {
        IndexableField docField = doc.getField(field.getName());
        if (docField != null) {
          field.setAccessible(true);
          try {
            field.set(obj, TypedQueryBuilders.get(field.getType())
                .convertDocFieldToJavaType(docField));
          } catch (IllegalAccessException e) {
            throw new RuntimeException("failed to set " + field + " with " + docField);
          }
        }
      }

      return obj;
    }).toList();
  }
}
