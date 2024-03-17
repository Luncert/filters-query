package org.luncert.filtersquery.api;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.luncert.filtersquery.api.criteria.FiltersQueryBuilderQueryCriteriaImpl;
import org.luncert.filtersquery.api.criteria.QueryCriteria;
import org.luncert.filtersquery.api.grammar.FiltersQueryLexer;
import org.luncert.filtersquery.api.grammar.FiltersQueryParser;

public abstract class FiltersQueryEngine {

  protected static <T extends FiltersQueryBuilder.Result> T buildQuery(
      String criteria, FiltersQueryBuilder queryBuilder) {
    var tree = toParseTree(criteria);
    var listener = new FiltersQueryListenerImpl(queryBuilder);
    var walker = new ParseTreeWalker();
    walker.walk(listener, tree);

    return queryBuilder.build();
  }

  public static QueryCriteria parse(String criteria) {
    var builder = new FiltersQueryBuilderQueryCriteriaImpl();
    FiltersQueryBuilderQueryCriteriaImpl.ResultImpl result = buildQuery(criteria, builder);
    return result.getQueryCriteria();
  }

  private static ParseTree toParseTree(String criteria) {
    FiltersQueryLexer lexer = new FiltersQueryLexer(CharStreams.fromString(criteria));
    lexer.removeErrorListeners();
    lexer.addErrorListener(ThrowingErrorListener.INSTANCE);

    FiltersQueryParser parser = new FiltersQueryParser(new CommonTokenStream(lexer));
    parser.removeErrorListeners();
    parser.addErrorListener(ThrowingErrorListener.INSTANCE);

    return parser.filtersQuery();
  }
}
