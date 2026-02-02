package org.luncert.filtersquery.jpaimpl.exception;

import org.luncert.filtersquery.api.exception.FiltersQueryException;

public class InvalidJoinPropertyException extends FiltersQueryException {

  public InvalidJoinPropertyException(String propertyName) {
    super(propertyName);
  }
}
