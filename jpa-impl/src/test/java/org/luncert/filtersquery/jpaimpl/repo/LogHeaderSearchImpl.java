package org.luncert.filtersquery.jpaimpl.repo;

import jakarta.persistence.EntityManager;
import org.luncert.filtersquery.jpaimpl.JpaFiltersQueryEngine;
import org.luncert.filtersquery.jpaimpl.model.LogHeader;
import org.springframework.stereotype.Component;

@Component
public class LogHeaderSearchImpl extends JpaFiltersQueryEngine<LogHeader> {

  public LogHeaderSearchImpl(EntityManager em) {
    super(em);
  }
}
