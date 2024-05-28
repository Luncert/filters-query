package org.luncert.filtersquery.jpaimpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.luncert.filtersquery.api.exception.FiltersQueryException;
import org.luncert.filtersquery.api.exception.FiltersQuerySyntaxException;
import org.luncert.filtersquery.api.exception.IllegalParameterException;
import org.luncert.filtersquery.jpaimpl.model.LogHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class QueryEngineTest {

  @Autowired
  private JpaFiltersQueryEngine<LogHeader> searchEngine;

  @Test(expected = FiltersQuerySyntaxException.class)
  public void testCriteriaSyntaxError() {
    searchEngine.search("filter by asd");
  }

  @Test(expected = FiltersQueryException.class)
  public void limitMissingOnNonPageableRequest() {
    searchEngine.search("filter by id=1 offset 3");
  }

  @Test(expected = FiltersQueryException.class)
  public void offsetMissingOnNonPageableRequest() {
    searchEngine.search("filter by id=1 limit 3");
  }

  @Test(expected = FiltersQueryException.class)
  public void applyLimitAndOffsetOnNonPageableRequest() {
    searchEngine.search("filter by id=1 offset 2 limit 3");
  }

  @Test(expected = FiltersQueryException.class)
  public void limitMissingOnPageableRequest() {
    searchEngine.searchPages("filter by id=1 offset 3");
  }

  @Test(expected = FiltersQueryException.class)
  public void offsetMissingOnPageableRequest() {
    searchEngine.searchPages("filter by id=1 limit 3");
  }

  @Test(expected = FiltersQueryException.class)
  public void limitAndOffsetMissingOnPageableRequest() {
    searchEngine.searchPages("filter by id=1");
  }

  @Test(expected = IllegalParameterException.class)
  public void invalidLimitOnPageableRequest() {
    searchEngine.searchPages("filter by id=1 offset 1 limit -1");
  }

  @Test(expected = IllegalParameterException.class)
  public void invalidOffsetOnPageableRequest() {
    searchEngine.searchPages("filter by id=1 offset -1 limit 2");
  }
}
