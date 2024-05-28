package org.luncert.filtersquery.api;

import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.Token;
import org.luncert.filtersquery.api.grammar.FiltersQueryParser;

public interface FiltersQueryBuilder {

  void associate(List<String> targets);

  void enterParentheses();

  void exitParentheses();

  void operator(Token operator);

  void equal(String name, FiltersQueryParser.PropertyValueWithReferenceBoolNullContext value);

  /**
   * Assert column not equal to specified value, not null and not empty.
   */
  void notEqual(String name, FiltersQueryParser.PropertyValueWithReferenceBoolNullContext value);

  /**
   * Assert column not null and not empty.
   */
  void empty(String name);

  void notEmpty(String name);

  void in(String name, List<FiltersQueryParser.PropertyValueWithReferenceBoolNullContext> values);

  void greaterThanEqual(String name, FiltersQueryParser.PropertyValueWithReferenceContext value);

  void greaterThan(String name, FiltersQueryParser.PropertyValueWithReferenceContext value);

  void lessThanEqual(String name, FiltersQueryParser.PropertyValueWithReferenceContext value);

  void lessThan(String name, FiltersQueryParser.PropertyValueWithReferenceContext value);

  void between(
      String name,
      FiltersQueryParser.PropertyValueContext startValue,
      FiltersQueryParser.PropertyValueContext endValue
  );

  void startsWith(String name, FiltersQueryParser.StringPropertyValueContext value);

  void endsWith(String name, FiltersQueryParser.StringPropertyValueContext value);

  void like(String name, FiltersQueryParser.StringPropertyValueContext value);

  /**
   * Add order.
   *
   * @param name      order by field name
   * @param direction optional, ASC or DESC
   */
  void order(String name, Token direction);

  void offset(int offset);

  void limit(int limit);

  void end();

  <T extends Result> T build();

  interface Result {
  }
}
