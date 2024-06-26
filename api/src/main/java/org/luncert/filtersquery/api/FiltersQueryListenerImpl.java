package org.luncert.filtersquery.api;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.luncert.filtersquery.api.grammar.FiltersQueryBaseListener;
import org.luncert.filtersquery.api.grammar.FiltersQueryParser;

@RequiredArgsConstructor
public class FiltersQueryListenerImpl extends FiltersQueryBaseListener {

  private final FiltersQueryBuilder queryBuilder;
  @Override
  public void exitFiltersQuery(FiltersQueryParser.FiltersQueryContext ctx) {
    queryBuilder.end();
  }

  @Override
  public void enterAssociateTargets(FiltersQueryParser.AssociateTargetsContext ctx) {
    var targets = ctx.children.stream()
        .filter(c -> c instanceof FiltersQueryParser.AssociateTargetContext)
        .map(c -> ((FiltersQueryParser.AssociateTargetContext) c).IDENTIFIER().getText()).toList();
    queryBuilder.associate(targets);
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
    boolean isCommaFirst = true;
    for (ParseTree child : ctx.betweenCriteriaValue().children) {
      if (child instanceof FiltersQueryParser.PropertyValueContext) {
        isCommaFirst = false;
        break;
      } else if (child instanceof TerminalNode node) {
        if (FiltersQueryParser.VOCABULARY.getSymbolicName(
            node.getSymbol().getType()).equals("COMMA")) {
          break;
        }
      }
    }

    queryBuilder.between(ctx.propertyName().getText(),
        isCommaFirst ? null : ctx.betweenCriteriaValue().propertyValue(0),
        ctx.betweenCriteriaValue().propertyValue(isCommaFirst ? 0 : 1)
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
    queryBuilder.startsWith(ctx.propertyName().getText(), ctx.stringPropertyValue());
  }

  @Override
  public void enterEndsWithCriteria(FiltersQueryParser.EndsWithCriteriaContext ctx) {
    queryBuilder.endsWith(ctx.propertyName().getText(), ctx.stringPropertyValue());
  }

  @Override
  public void enterLikeCriteria(FiltersQueryParser.LikeCriteriaContext ctx) {
    queryBuilder.like(ctx.propertyName().getText(), ctx.stringPropertyValue());
  }

  @Override
  public void enterEqualCriteria(FiltersQueryParser.EqualCriteriaContext ctx) {
    queryBuilder.equal(ctx.propertyName().getText(), ctx.propertyValueWithReferenceBoolNull());
  }

  @Override
  public void enterNotEqualCriteria(FiltersQueryParser.NotEqualCriteriaContext ctx) {
    queryBuilder.notEqual(ctx.propertyName().getText(), ctx.propertyValueWithReferenceBoolNull());
  }

  @Override
  public void enterGreaterThanCriteria(FiltersQueryParser.GreaterThanCriteriaContext ctx) {
    queryBuilder.greaterThan(ctx.propertyName().getText(), ctx.propertyValueWithReference());
  }


  @Override
  public void enterGreaterEqualThanCriteria(
      FiltersQueryParser.GreaterEqualThanCriteriaContext ctx) {
    queryBuilder.greaterThanEqual(ctx.propertyName().getText(), ctx.propertyValueWithReference());
  }

  @Override
  public void enterLessThanCriteria(FiltersQueryParser.LessThanCriteriaContext ctx) {
    queryBuilder.lessThan(ctx.propertyName().getText(), ctx.propertyValueWithReference());
  }

  @Override
  public void enterLessEqualThanCriteria(FiltersQueryParser.LessEqualThanCriteriaContext ctx) {
    queryBuilder.lessThanEqual(ctx.propertyName().getText(), ctx.propertyValueWithReference());
  }

  @Override
  public void enterInCriteria(FiltersQueryParser.InCriteriaContext ctx) {
    queryBuilder.in(ctx.propertyName().getText(),
        ctx.propertyValueList().propertyValueWithReferenceBoolNull());
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
