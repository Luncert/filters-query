package org.luncert.filtersquery.jpaimpl;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Collection;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.lks.junitwithdata.common.TestKit;
import org.luncert.filtersquery.jpaimpl.model.Label;
import org.luncert.filtersquery.jpaimpl.model.LogHeader;
import org.luncert.filtersquery.jpaimpl.model.Tag;
import org.luncert.filtersquery.jpaimpl.repo.ILabelRepo;
import org.luncert.filtersquery.jpaimpl.repo.ILogHeaderRepo;
import org.luncert.filtersquery.jpaimpl.repo.ITagRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

@RunWith(Parameterized.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class QueryEngineParameterizedTest {

  @ClassRule
  public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

  @Rule
  public final SpringMethodRule springMethodRule = new SpringMethodRule();

  @Autowired
  private JpaFiltersQueryEngine<LogHeader> searchEngine;

  @Autowired
  private ILogHeaderRepo logHeaderRepo;

  @Autowired
  private ITagRepo tagRepo;

  @Autowired
  private ILabelRepo labelRepo;

  private final TestKit.TestDataLoader loader = TestKit.load(this);

  private final String testCase;

  public QueryEngineParameterizedTest(String testCase) {
    this.testCase = testCase;
  }

  @Before
  public void before() {
    List<LogHeader> logHeaders = TestKit.loadData("unit-test/LogHeaderData.json", new TypeReference<>() { });
    logHeaderRepo.saveAll(logHeaders);
    List<Tag> tags = TestKit.loadData("unit-test/TagData.json", new TypeReference<>() { });
    tagRepo.saveAll(tags);
    List<Label> labels = TestKit.loadData("unit-test/LabelData.json", new TypeReference<>() { });
    labelRepo.saveAll(labels);
  }

  @After
  public void after() {
    logHeaderRepo.deleteAll();
  }

  @SneakyThrows
  @Test
  public void test() {
    String criteria = loader.extractAs(testCase + ".criteria", String.class);
    List<Long> results;
    if (testCase.endsWith("offset_and_limit")) {
      results = searchEngine.searchPages(criteria).stream().map(LogHeader::getId).toList();
    } else {
      results = searchEngine.search(criteria).stream().map(LogHeader::getId).toList();
    }
    List<Long> expectData = loader.extractAs(testCase + ".expected", new TypeReference<>() {});
    TestKit.assertEquals(results, expectData);
  }

  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object> data() {
    return List.of(
        // "case_without_filters_with_offset_and_limit",
        // "case_createdAt_equal",
        // "case_categoryId_and_subCategoryId_equal",
        // "case_externalReference_or_severity_equal",
        // "case_externalReference_or_severity_equal_with_offset_and_limit",
        // "case_externalReference_or_severity_equal_sort_by_id_desc",
        // "case_externalReference_or_severity_equal_with_sort_offset_and_limit",
        // "case_subCategoryId_not_equal",
        // "case_id_not_equal",
        // "case_subCategoryId_empty",
        // "case_subCategoryId_not_empty",
        // "case_categoryId_lessThan",
        // "case_id_greaterEqualThan",
        // "case_id_greaterThan",
        // "case_id_lessEqualThan",
        // "case_id_lessThan",
        // "case_id_between",
        // "case_externalReference_startsWith",
        // "case_externalReference_endsWith",
        // "case_createdAt_like",
        // "case_paren",
        // "case_in",
        // "case_subCategoryId_equal_null",
        // "case_subCategoryId_not_null",
        // "case_subCategoryId_in_null",
        // "case_bool_equal_true",
        // "case_bool_equal_false",
        // "case_bool_not_null",
        //"case_join",
        "case_join_collection"
    );
  }
}
