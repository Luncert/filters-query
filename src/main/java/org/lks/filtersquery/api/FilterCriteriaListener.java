//package org.lks.filtersquery.api;
//
//import lombok.RequiredArgsConstructor;
//import org.antlr.v4.runtime.tree.TerminalNode;
//
//@RequiredArgsConstructor
//public class FilterCriteriaListener<E> extends FilterCriteriaListener {
//
//  private final FiltersQueryBuilder<E> builder;
//
//  @Override
//  public void exitFilterCriteria(com.lks.filtersquery.api.grammar.FilterCriteriaParser.FilterCriteriaContext ctx) {
//    builder.end();
//  }
//
//  @Override
//  public void enterWrappedFilters(FilterCriteriaParser.WrappedFiltersContext ctx) {
//    builder.enterParen();
//  }
//
//  @Override
//  public void exitWrappedFilters(FilterCriteriaParser.WrappedFiltersContext ctx) {
//    builder.exitParen();
//  }
//
//  @Override
//  public void enterBoolOperator(FilterCriteriaParser.BoolOperatorContext ctx) {
//    builder.operator(((TerminalNode) ctx.getChild(1)).getSymbol());
//  }
//
//  @Override
//  public void enterOrder(FilterCriteriaParser.OrderContext ctx) {
//    builder.order(ctx.propertyName().getText(), ((TerminalNode) ctx.getChild(1)).getSymbol());
//  }
//
//  @Override
//  public void enterOffset(FilterCriteriaParser.OffsetContext ctx) {
//    builder.offset(Integer.parseInt(ctx.getChild(ctx.getChildCount() - 1).getText()));
//  }
//
//  @Override
//  public void enterLimit(FilterCriteriaParser.LimitContext ctx) {
//    builder.limit(Integer.parseInt(ctx.getChild(ctx.getChildCount() - 1).getText()));
//  }
//
//  @Override
//  public void enterGreaterEqualThanCriteria(
//      FilterCriteriaParser.GreaterEqualThanCriteriaContext ctx) {
//    builder.greaterEqualThan(ctx.propertyName().getText(), ctx.propertyValue().getChild(0));
//  }
//
//  @Override
//  public void enterLessEqualThanCriteria(FilterCriteriaParser.LessEqualThanCriteriaContext ctx) {
//    builder.lessEqualThan(ctx.propertyName().getText(), ctx.propertyValue().getChild(0));
//  }
//
//  @Override
//  public void enterLessThanCriteria(FilterCriteriaParser.LessThanCriteriaContext ctx) {
//    builder.lessThan(ctx.propertyName().getText(), ctx.propertyValue().getChild(0));
//  }
//
//  @Override
//  public void enterBetweenCriteria(FilterCriteriaParser.BetweenCriteriaContext ctx) {
//    builder.between(ctx.propertyName().getText(),
//          ctx.betweenCriteriaValue().propertyValue(0).getChild(0),
//          ctx.betweenCriteriaValue().propertyValue(1).getChild(0)
//    );
//  }
//
//  @Override
//  public void enterGreaterThanCriteria(FilterCriteriaParser.GreaterThanCriteriaContext ctx) {
//    builder.greaterThan(ctx.propertyName().getText(), ctx.propertyValue().getChild(0));
//  }
//
//  @Override
//  public void enterStartsWithCriteria(FilterCriteriaParser.StartsWithCriteriaContext ctx) {
//    builder.startsWith(ctx.propertyName().getText(), ctx.stringPropertyValue().getChild(0));
//  }
//
//  @Override
//  public void enterEndsWithCriteria(FilterCriteriaParser.EndsWithCriteriaContext ctx) {
//    builder.endsWith(ctx.propertyName().getText(), ctx.stringPropertyValue().getChild(0));
//  }
//
//  @Override
//  public void enterLikeCriteria(FilterCriteriaParser.LikeCriteriaContext ctx) {
//    builder.like(ctx.propertyName().getText(), ctx.stringPropertyValue().getChild(0));
//  }
//
//  @Override
//  public void enterNotEqualCriteria(FilterCriteriaParser.NotEqualCriteriaContext ctx) {
//    builder.notEqual(ctx.propertyName().getText(), ctx.propertyValue().getChild(0));
//  }
//
//  @Override
//  public void enterEqualCriteria(FilterCriteriaParser.EqualCriteriaContext ctx) {
//    builder.equal(ctx.propertyName().getText(), ctx.propertyValue().getChild(0));
//  }
//
//  @Override
//  public void enterEmptyCriteria(FilterCriteriaParser.EmptyCriteriaContext ctx) {
//    builder.empty(ctx.propertyName().getText());
//  }
//
//  @Override
//  public void enterNotEmptyCriteria(FilterCriteriaParser.NotEmptyCriteriaContext ctx) {
//    builder.notEmpty(ctx.propertyName().getText());
//  }
//}
