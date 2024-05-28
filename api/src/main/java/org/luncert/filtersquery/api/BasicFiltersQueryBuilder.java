package org.luncert.filtersquery.api;

import java.util.List;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.luncert.filtersquery.api.grammar.FiltersQueryParser;

public abstract class BasicFiltersQueryBuilder implements FiltersQueryBuilder {

  @Override
  public void associate(List<String> targets) {
    throw new UnsupportedOperationException();
  }

  protected String getTokenName(Token token) {
    return FiltersQueryParser.VOCABULARY.getSymbolicName(token.getType());
  }

  protected String getTokenName(int tokenType) {
    return FiltersQueryParser.VOCABULARY.getSymbolicName(tokenType);
  }

  protected String getLiteral(ParseTree value) {
    var s = resolveStringLiteral(value);
    if (s != null) {
      return Utils.unwrap(s, '"');
    }

    s = resolveDecimalLiteral(value);
    if (s != null) {
      return s;
    }

    return value.getText();
  }

  protected String resolveStringLiteral(ParseTree value) {
    if (value instanceof FiltersQueryParser.PropertyValueWithReferenceBoolNullContext ctx) {
      value = ctx.propertyValue();
    } else if (value instanceof FiltersQueryParser.PropertyValueWithReferenceContext ctx) {
      value = ctx.propertyValue();
    }

    if (value instanceof FiltersQueryParser.PropertyValueContext ctx) {
      if (ctx.INTERPRETED_STRING_LIT() != null) {
        return ctx.INTERPRETED_STRING_LIT().getText();
      }
    } else if (value instanceof FiltersQueryParser.StringPropertyValueContext ctx) {
      return ctx.INTERPRETED_STRING_LIT().getText();
    }

    return null;
  }

  protected String resolveDecimalLiteral(ParseTree value) {
    if (value instanceof FiltersQueryParser.PropertyValueWithReferenceContext ctx) {
      value = ctx.propertyValue();
      if (value == null) {
        return null;
      }
    }

    if (value instanceof FiltersQueryParser.PropertyValueContext ctx) {
      if (ctx.DECIMAL_LIT() != null) {
        return ctx.DECIMAL_LIT().getText();
      }
      if (ctx.ZERO() != null) {
        return ctx.ZERO().getText();
      }
    }

    return null;
  }
}
