package org.lks.filtersquery.api;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.lks.filtersquery.api.grammar.FiltersQueryBaseListener;
import org.lks.filtersquery.api.grammar.FiltersQueryParser;

@RequiredArgsConstructor
public class FiltersQueryListenerImpl extends FiltersQueryBaseListener {

  private final FiltersQueryBuilder queryBuilder;

  @Override
  public void exitFiltersQuery(FiltersQueryParser.FiltersQueryContext ctx) {
    queryBuilder.end();
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
    queryBuilder.operator(((TerminalNode) ctx.getChild(1)).getSymbol());
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
    queryBuilder.equal(ctx.propertyName().getText(), ctx.propertyValue().getChild(0));
  }

  @Override
  public void enterNotEqualCriteria(FiltersQueryParser.NotEqualCriteriaContext ctx) {
    queryBuilder.notEqual(ctx.propertyName().getText(), ctx.propertyValue().getChild(0));
  }

  @Override
  public void enterGreaterThanCriteria(FiltersQueryParser.GreaterThanCriteriaContext ctx) {
    queryBuilder.greaterThan(ctx.propertyName().getText(), ctx.propertyValue().getChild(0));
  }


  @Override
  public void enterGreaterEqualThanCriteria(
      FiltersQueryParser.GreaterEqualThanCriteriaContext ctx) {
    queryBuilder.greaterEqualThan(ctx.propertyName().getText(), ctx.propertyValue().getChild(0));
  }

  @Override
  public void enterLessThanCriteria(FiltersQueryParser.LessThanCriteriaContext ctx) {
    queryBuilder.lessThan(ctx.propertyName().getText(), ctx.propertyValue().getChild(0));
  }

  @Override
  public void enterLessEqualThanCriteria(FiltersQueryParser.LessEqualThanCriteriaContext ctx) {
    queryBuilder.lessEqualThan(ctx.propertyName().getText(), ctx.propertyValue().getChild(0));
  }

  @Override
  public void enterOrder(FiltersQueryParser.OrderContext ctx) {
    queryBuilder.order(ctx.propertyName().getText(), ((TerminalNode) ctx.getChild(1)).getSymbol());
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
