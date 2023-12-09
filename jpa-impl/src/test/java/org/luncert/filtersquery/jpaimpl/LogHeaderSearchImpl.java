package org.luncert.filtersquery.jpaimpl;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

@Component
public class LogHeaderSearchImpl extends JpaFiltersQueryEngine<LogHeader> {

  public LogHeaderSearchImpl(EntityManager em) {
    super(em);
  }
}
