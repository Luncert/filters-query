package org.luncert.filtersquery.api;

import java.util.Map;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.luncert.filtersquery.api.grammar.FiltersQueryParser;

public abstract class BasicFiltersQueryBuilder implements FiltersQueryBuilder {

  @Override
  public void defineAlias(Map<String, String> alias) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void conjunctionEqual(String left, String right) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void conjunctionNotEqual(String left, String right) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void enterConjunctionParentheses() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void exitConjunctionParentheses() {
    throw new UnsupportedOperationException();
  }

  protected String getTokenName(Token token) {
    return FiltersQueryParser.VOCABULARY.getSymbolicName(token.getType());
  }

  protected String getTokenName(int tokenType) {
    return FiltersQueryParser.VOCABULARY.getSymbolicName(tokenType);
  }

  protected String getLiteral(ParseTree value) {
    return isStringLiteral(value)
        ? Utils.unwrap(value.getText(), '"')
        : value.getText();
  }

  protected boolean isStringLiteral(ParseTree value) {
    return getTokenName(((TerminalNode) value).getSymbol())
        .equals("INTERPRETED_STRING_LIT");
  }

  protected boolean isDecimalLiteral(ParseTree value) {
    var symbol = getTokenName(((TerminalNode) value).getSymbol());
    return symbol.equals("DECIMAL_LIT") || symbol.equals("ZERO");
  }
}
