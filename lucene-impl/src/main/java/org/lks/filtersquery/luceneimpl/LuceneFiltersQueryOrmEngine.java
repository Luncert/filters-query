package org.lks.filtersquery.luceneimpl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.lks.filtersquery.luceneimpl.impl.TypedQueryBuilders;

public class LuceneFiltersQueryOrmEngine<E> extends LuceneFiltersQueryEngine {

  private final Class<E> entityType;

  @SuppressWarnings("unchecked")
  public LuceneFiltersQueryOrmEngine() {
    this.entityType = (Class<E>) ((ParameterizedType) getClass()
        .getGenericSuperclass()).getActualTypeArguments()[0];
  }

  public List<E> search(IndexReader indexReader, String criteria) throws IOException {
    return search(indexReader, criteria, this::convertDocumentToEntity);
  }

  public <R> List<R> search(IndexReader indexReader,
                        String criteria,
                        Function<Document, R> resultMapper) throws IOException {
    FiltersQueryBuilderLuceneImpl.ResultImpl result = buildQuery(criteria, entityType);

    int offset = result.getOffset() == null ? 0 : result.getOffset();
    int limit = result.getLimit() == null ? Integer.MAX_VALUE : result.getLimit();

    IndexSearcher searcher = new IndexSearcher(indexReader);
    TopDocs topDocs = result.getSort() == null
        ? searcher.search(result.getQuery(), offset + limit)
        : searcher.search(result.getQuery(), offset + limit, result.getSort());
    return Arrays.stream(topDocs.scoreDocs,
            offset, Math.min(topDocs.scoreDocs.length, offset + limit))
        .map(scoreDoc -> {
          try {
            return indexReader.document(scoreDoc.doc);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .map(resultMapper)
        .toList();
  }

  private E convertDocumentToEntity(Document doc) {
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
  }
}
