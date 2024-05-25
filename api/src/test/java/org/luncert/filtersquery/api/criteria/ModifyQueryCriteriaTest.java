package org.luncert.filtersquery.api.criteria;

import static org.luncert.filtersquery.api.criteria.Reference.ref;
import static org.luncert.filtersquery.api.criteria.Value.number;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.luncert.filtersquery.api.FiltersQueryEngine;
import org.luncert.filtersquery.api.criteria.predicate.AndPredicate;

public class ModifyQueryCriteriaTest {

  @Test
  public void test() {
    var input = "filter by ((a between [0, 100] and b like \"hi\") or c >= 10) sort by a asc, b desc offset 10 limit 10";
    var expected = "filter by ((a between [0, 100] and b like \"hi\" and x > 10) or c >= 10) sort by a asc, b desc offset 10 limit 10";

    var queryCriteria = FiltersQueryEngine.parse(input);

    queryCriteria.modifyPredicate(p -> {
      var children = new ArrayList<Node>();
      int i = 0;
      while (i < p.getChildenSize()) {
        var child = p.getChild(i);
        if (child instanceof AndPredicate) {
          child.insertChild(child.getChildenSize(), ref("x").gt(number(10)));
        }
        children.add(child);
        i++;
      }
      return Predicate.or(children.toArray(new Predicate[0]));
    });

    Assert.assertEquals(expected, queryCriteria.toString());
  }

  @Test
  public void split() {
    var input = "filter by ((a between [0, 100] and b like \"hi\") or c >= 10) sort by a asc, b desc offset 10 limit 10";
    var expected = "filter by (a between [0, 100] or c >= 10) sort by a asc, b desc offset 10 limit 10";

    var queryCriteria = FiltersQueryEngine.parse(input);

    var result = queryCriteria.splitPredicate("b");
    Assert.assertEquals(queryCriteria.toString(), expected);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(result.get(0).toString(), "b like \"hi\"");
  }
}
