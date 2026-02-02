package org.luncert.filtersquery.jpaimpl.exception;

import org.luncert.filtersquery.api.exception.FiltersQueryCirtiraException;

public class InvalidJoinTargetException extends FiltersQueryCirtiraException {

  public InvalidJoinTargetException(String target) {
    super("invalid join target " + target);
  }
}
