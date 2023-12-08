package org.lks.filtersquery.luceneimpl;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.lks.junitwithdata.common.TestKit;

@RunWith(Parameterized.class)
public class QueryEngineParameterizedTest {

  private final Directory directory = new RAMDirectory();
  private final LuceneFiltersQueryEngine queryEngine = new LuceneFiltersQueryEngine();

  private final TestKit.TestDataLoader loader = TestKit.load(this);

  private final String testCase;

  public QueryEngineParameterizedTest(String testCase) {
    this.testCase = testCase;
  }

  @Before
  public void before() {
    List<LogHeader> data = TestKit.loadData("unit-test/Data.json", new TypeReference<>() {
    });
    try (IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()))) {
      for (LogHeader source : data) {
        Document document = new Document();
        document.add(new NumericDocValuesField("id", source.getId()));
        document.add(new LongPoint("id", source.getId()));
        document.add(new StoredField("id", source.getId()));
        document.add(new LongPoint("createdAt", source.getCreatedAt()));
        document.add(new StoredField("createdAt", source.getCreatedAt()));
        document.add(new StringField("externalReference",
            wrapNull(source.getExternalReference()), Field.Store.YES));
        document.add(new StringField("severity",
            wrapNull(source.getSeverity()), Field.Store.YES));
        document.add(new StringField("categoryId",
            wrapNull(source.getCategoryId()), Field.Store.YES));
        document.add(new StringField("subCategoryId",
            wrapNull(source.getSubCategoryId()), Field.Store.YES));
        indexWriter.addDocument(document);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @After
  public void after() {
    try (IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()))) {
      indexWriter.deleteAll();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @SneakyThrows
  @Test
  public void test() {
    String criteria = loader.extractAs(testCase + ".criteria", String.class);
    FiltersQueryBuilderLuceneImpl.ResultImpl result = queryEngine.buildQuery(criteria, LogHeader.class);
    try (IndexReader indexReader = DirectoryReader.open(directory)) {
      IndexSearcher searcher = new IndexSearcher(indexReader);
      int limit = result.getLimit() == null ? 1000 : result.getLimit();
      TopDocs topDocs = result.getSort() == null
          ? searcher.search(result.getQuery(), limit)
          : searcher.search(result.getQuery(), limit, result.getSort());

      List<Long> ids = Arrays.stream(topDocs.scoreDocs).map(scoreDoc -> {
        try {
          return indexReader.document(scoreDoc.doc)
              .getField("id").numericValue().longValue();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }).toList();
      List<Long> expectData = loader.extractAs(testCase + ".expected", new TypeReference<>() {
      });
      TestKit.assertEquals(ids, expectData);
    }
  }

  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object> data() {
    return List.of(
        "case_without_filters_with_limit",
        "case_createdAt_equal",
        "case_categoryId_and_subCategoryId_equal",
        "case_externalReference_or_severity_equal",
        "case_externalReference_or_severity_equal_with_limit",
        "case_externalReference_or_severity_equal_sort_by_id",
        "case_externalReference_or_severity_equal_with_sort_limit",
        "case_subCategoryId_not_equal",
        "case_id_not_equal",
        "case_subCategoryId_empty",
        "case_subCategoryId_not_empty",
        "case_categoryId_lessThan",
        "case_id_greaterEqualThan",
        "case_id_greaterThan",
        "case_id_lessEqualThan",
        "case_id_lessThan",
        "case_id_between",
        "case_externalReference_startsWith",
        "case_externalReference_endsWith",
        "case_paren"
    );
  }

  private String wrapNull(String value) {
    return StringUtils.isEmpty(value) ? "[NULL_VALUE]" : value;
  }
}
