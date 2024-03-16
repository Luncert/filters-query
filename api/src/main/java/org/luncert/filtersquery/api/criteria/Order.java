package org.luncert.filtersquery.api.criteria;

import lombok.Getter;

@Getter
public enum Order {

  ASC("asc"),
  DESC("desc");

  private final String identicalName;

  Order(String identicalName) {
    this.identicalName = identicalName;
  }
}
