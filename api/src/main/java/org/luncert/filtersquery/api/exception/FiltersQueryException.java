package org.luncert.filtersquery.api.exception;

public class FiltersQueryException extends RuntimeException {

  public FiltersQueryException() {
  }

  public FiltersQueryException(String message) {
    super(message);
  }

  public FiltersQueryException(Throwable cause) {
    super(cause);
  }
}
