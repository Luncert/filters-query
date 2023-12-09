package org.lks.filtersquery.predicateimpl;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.lks.junitwithdata.common.TestKit;

@RunWith(Parameterized.class)
public class QueryEngineParameterizedTest {

  private final TestKit.TestDataLoader loader = TestKit.load(this);

  private final String testCase;

  public QueryEngineParameterizedTest(String testCase) {
    this.testCase = testCase;
  }

  @SneakyThrows
  @Test
  public void test() {
    List<LogHeader> data = TestKit.loadData("unit-test/Data.json", new TypeReference<>() {});
    String criteria = loader.extractAs(testCase + ".criteria", String.class);
    Predicate<LogHeader> predicate = PredicateFiltersQueryEngine.buildQuery(criteria, LogHeader.class);
    List<Long> ids = data.stream().filter(predicate).map(LogHeader::getId).toList();
    List<Long> expectData = loader.extractAs(testCase + ".expected", new TypeReference<>() {});
    TestKit.assertEquals(ids, expectData);
  }

  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object> data() {
    return List.of(
        "case_without_filters",
        "case_createdAt_equal",
        "case_createdAt_lessThan",
        "case_createdAt_lessEqualThan",
        "case_createdAt_greaterThan",
        "case_createdAt_greaterEqualThan",
        "case_categoryId_and_subCategoryId_equal",
        "case_externalReference_or_severity_equal",
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
}
