package org.luncert.filtersquery.api;

import java.util.List;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

public interface FiltersQueryBuilder {

  void enterParentheses();

  void exitParentheses();

  void operator(Token operator);

  void equal(String name, ParseTree value);

  /**
   * Assert column not equal to specified value, not null and not empty.
   */
  void notEqual(String name, ParseTree value);

  /**
   * Assert column not null and not empty.
   */
  void empty(String name);

  void notEmpty(String name);

  void in(String name, List<ParseTree> values);

  void greaterThanEqual(String name, ParseTree value);

  void greaterThan(String name, ParseTree value);

  void lessThanEqual(String name, ParseTree value);

  void lessThan(String name, ParseTree value);

  void between(String name, ParseTree startValue, ParseTree endValue);

  void startsWith(String name, ParseTree value);

  void endsWith(String name, ParseTree value);

  void like(String name, ParseTree value);

  /**
   * Add order.
   *
   * @param name order by field name
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
