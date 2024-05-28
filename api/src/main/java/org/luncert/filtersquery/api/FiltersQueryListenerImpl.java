package org.luncert.filtersquery.api;

import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.luncert.filtersquery.api.grammar.FiltersQueryBaseListener;
import org.luncert.filtersquery.api.grammar.FiltersQueryParser;

@RequiredArgsConstructor
public class FiltersQueryListenerImpl extends FiltersQueryBaseListener {

  private final FiltersQueryBuilder queryBuilder;
  private boolean inConjunction;

  @Override
  public void exitFiltersQuery(FiltersQueryParser.FiltersQueryContext ctx) {
    queryBuilder.end();
  }

  @Override
  public void enterConjunction(FiltersQueryParser.ConjunctionContext ctx) {
    inConjunction = true;
  }

  @Override
  public void exitConjunction(FiltersQueryParser.ConjunctionContext ctx) {
    inConjunction = false;
  }

  @Override
  public void enterAliasList(FiltersQueryParser.AliasListContext ctx) {
    var alias = new HashMap<String, String>();
    String entityName = null;
    for (int i = 0; i < ctx.getChildCount(); i++) {
      var child = ctx.getChild(i);
      if (child instanceof FiltersQueryParser.EntityNameContext) {
        entityName = child.getText();
      } else if (child instanceof FiltersQueryParser.EntityAliasNameContext) {
        assert entityName != null;
        alias.put(entityName, child.getText());
        entityName = null;
      }
    }
    queryBuilder.defineAlias(alias);
  }

  @Override
  public void enterConjunctionEqualCriteria(FiltersQueryParser.ConjunctionEqualCriteriaContext ctx) {
    queryBuilder.conjunctionEqual(
        ctx.propertyName().get(0).getText(),
        ctx.propertyName().get(1).getText()
    );
  }

  @Override
  public void enterConjunctionNotEqualCriteria(FiltersQueryParser.ConjunctionNotEqualCriteriaContext ctx) {
    queryBuilder.conjunctionNotEqual(
        ctx.propertyName().get(0).getText(),
        ctx.propertyName().get(1).getText()
    );
  }

  @Override
  public void enterWrappedConjunctionFilters(FiltersQueryParser.WrappedConjunctionFiltersContext ctx) {
    queryBuilder.enterConjunctionParentheses();
  }

  @Override
  public void exitWrappedConjunctionFilters(FiltersQueryParser.WrappedConjunctionFiltersContext ctx) {
    queryBuilder.exitConjunctionParentheses();
  }

  @Override
  public void enterWrappedFilters(FiltersQueryParser.WrappedFiltersContext ctx) {
    queryBuilder.enterParentheses();
  }

  @Override
  public void exitWrappedFilters(FiltersQueryParser.WrappedFiltersContext ctx) {
    queryBuilder.exitParentheses();
  }

  @Override
  public void enterBoolOperator(FiltersQueryParser.BoolOperatorContext ctx) {
    queryBuilder.operator(((TerminalNode) ctx.getChild(1)).getSymbol(), inConjunction);
  }

  @Override
  public void enterBetweenCriteria(FiltersQueryParser.BetweenCriteriaContext ctx) {
    queryBuilder.between(ctx.propertyName().getText(),
        ctx.betweenCriteriaValue().propertyValue(0).getChild(0),
        ctx.betweenCriteriaValue().propertyValue(1).getChild(0)
    );
  }

  @Override
  public void enterEmptyCriteria(FiltersQueryParser.EmptyCriteriaContext ctx) {
    queryBuilder.empty(ctx.propertyName().getText());
  }

  @Override
  public void enterNotEmptyCriteria(FiltersQueryParser.NotEmptyCriteriaContext ctx) {
    queryBuilder.notEmpty(ctx.propertyName().getText());
  }

  @Override
  public void enterStartsWithCriteria(FiltersQueryParser.StartsWithCriteriaContext ctx) {
    queryBuilder.startsWith(ctx.propertyName().getText(), ctx.stringPropertyValue().getChild(0));
  }

  @Override
  public void enterEndsWithCriteria(FiltersQueryParser.EndsWithCriteriaContext ctx) {
    queryBuilder.endsWith(ctx.propertyName().getText(), ctx.stringPropertyValue().getChild(0));
  }

  @Override
  public void enterLikeCriteria(FiltersQueryParser.LikeCriteriaContext ctx) {
    queryBuilder.like(ctx.propertyName().getText(), ctx.stringPropertyValue().getChild(0));
  }

  @Override
  public void enterEqualCriteria(FiltersQueryParser.EqualCriteriaContext ctx) {
    queryBuilder.equal(ctx.propertyName().getText(), ctx.propertyValueWithReferenceBoolNull().getChild(0));
  }

  @Override
  public void enterNotEqualCriteria(FiltersQueryParser.NotEqualCriteriaContext ctx) {
    queryBuilder.notEqual(ctx.propertyName().getText(), ctx.propertyValueWithReferenceBoolNull().getChild(0));
  }

  @Override
  public void enterGreaterThanCriteria(FiltersQueryParser.GreaterThanCriteriaContext ctx) {
    queryBuilder.greaterThan(ctx.propertyName().getText(), ctx.propertyValueWithReference().getChild(0));
  }


  @Override
  public void enterGreaterEqualThanCriteria(
      FiltersQueryParser.GreaterEqualThanCriteriaContext ctx) {
    queryBuilder.greaterThanEqual(ctx.propertyName().getText(), ctx.propertyValueWithReference().getChild(0));
  }

  @Override
  public void enterLessThanCriteria(FiltersQueryParser.LessThanCriteriaContext ctx) {
    queryBuilder.lessThan(ctx.propertyName().getText(), ctx.propertyValueWithReference().getChild(0));
  }

  @Override
  public void enterLessEqualThanCriteria(FiltersQueryParser.LessEqualThanCriteriaContext ctx) {
    queryBuilder.lessThanEqual(ctx.propertyName().getText(), ctx.propertyValueWithReference().getChild(0));
  }

  @Override
  public void enterInCriteria(FiltersQueryParser.InCriteriaContext ctx) {
    var values = ctx.propertyValueList().children.stream()
        .filter(v -> !(v instanceof TerminalNode))
        .map(v -> v.getChild(0))
        .toList();
    queryBuilder.in(ctx.propertyName().getText(), values);
  }

  @Override
  public void enterOrder(FiltersQueryParser.OrderContext ctx) {
    queryBuilder.order(ctx.propertyName().getText(), ctx.getChildCount() > 1
        ? ((TerminalNode) ctx.getChild(ctx.getChildCount() - 1)).getSymbol() : null);
  }

  @Override
  public void enterOffset(FiltersQueryParser.OffsetContext ctx) {
    queryBuilder.offset(Integer.parseInt(ctx.getChild(ctx.getChildCount() - 1).getText()));
  }

  @Override
  public void enterLimit(FiltersQueryParser.LimitContext ctx) {
    queryBuilder.limit(Integer.parseInt(ctx.getChild(ctx.getChildCount() - 1).getText()));
  }
}
