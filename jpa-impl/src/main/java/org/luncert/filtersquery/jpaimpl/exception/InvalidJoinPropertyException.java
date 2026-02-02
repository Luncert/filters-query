package org.luncert.filtersquery.jpaimpl.exception;

import org.luncert.filtersquery.api.exception.FiltersQueryCirtiraException;

public class InvalidJoinPropertyException extends FiltersQueryCirtiraException {

  public InvalidJoinPropertyException(String propertyName) {
    super("invalid join property " + propertyName);
  }
}
