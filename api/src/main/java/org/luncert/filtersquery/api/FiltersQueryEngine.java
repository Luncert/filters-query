package org.luncert.filtersquery.api;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.luncert.filtersquery.api.grammar.FiltersQueryLexer;
import org.luncert.filtersquery.api.grammar.FiltersQueryListener;
import org.luncert.filtersquery.api.grammar.FiltersQueryParser;

public abstract class FiltersQueryEngine {

  protected static <T extends FiltersQueryBuilder.Result> T buildQuery(
      String criteria, FiltersQueryBuilder criteriaBuilder) {
    FiltersQueryLexer lexer = new FiltersQueryLexer(CharStreams.fromString(criteria));
    lexer.removeErrorListeners();
    lexer.addErrorListener(ThrowingErrorListener.INSTANCE);

    FiltersQueryParser parser = new FiltersQueryParser(new CommonTokenStream(lexer));
    parser.removeErrorListeners();
    parser.addErrorListener(ThrowingErrorListener.INSTANCE);

    ParseTree tree = parser.filtersQuery();
    ParseTreeWalker walker = new ParseTreeWalker();

    FiltersQueryListener listener = new FiltersQueryListenerImpl(criteriaBuilder);
    walker.walk(listener, tree);

    return criteriaBuilder.build();
  }
}
