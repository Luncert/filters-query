package org.luncert.filtersquery.jpaimpl.exception;

import org.luncert.filtersquery.api.exception.FiltersQueryException;

public class InvalidJoinTargetException extends FiltersQueryException {

  public InvalidJoinTargetException(String target) {
    super(target);
  }
}
