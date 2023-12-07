package org.lks.filtersquery.api;

import org.antlr.v4.runtime.Token;
import org.lks.filtersquery.api.grammar.FiltersQueryParser;

public abstract class BasicFiltersQueryBuilder implements FiltersQueryBuilder {

  protected String getTokenName(Token token) {
    return FiltersQueryParser.VOCABULARY.getSymbolicName(token.getType());
  }

  protected String getTokenName(int tokenType) {
    return FiltersQueryParser.VOCABULARY.getSymbolicName(tokenType);
  }
}
