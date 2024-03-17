package org.luncert.filtersquery.api.criteria;

import static org.luncert.filtersquery.api.criteria.Predicate.and;
import static org.luncert.filtersquery.api.criteria.Predicate.or;
import static org.luncert.filtersquery.api.criteria.Reference.ref;
import static org.luncert.filtersquery.api.criteria.Value.literal;
import static org.luncert.filtersquery.api.criteria.Value.number;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.luncert.filtersquery.api.FiltersQueryEngine;
import org.luncert.filtersquery.api.criteria.dsl.QueryCriteriaBuilder;
import org.luncert.filtersquery.api.criteria.predicate.AndPredicate;

public class ModifyQueryCriteriaTest {

  @Test
  public void test() {
    var input = "filter by ((a between [0, 100] and b like \"hi\") or c >= 10) sort by a asc, b desc offset 10 limit 10";
    var expected = "filter by ((a between [0, 100] and b like \"hi\" and x > 10) or c >= 10) sort by a asc, b desc offset 10 limit 10";

    var queryCriteria = FiltersQueryEngine.parse(input);

    queryCriteria.modifyPredicate(p -> {
      var children = new ArrayList<Node>();
      for (Node child : p.getChildren()) {
        if (child instanceof AndPredicate) {
          var old = new ArrayList<>(child.getChildren());
          old.add(ref("x").gt(number(10)));
          children.add(Predicate.and(old.toArray(new Predicate[0])));
        } else {
          children.add(child);
        }
      }
      return Predicate.or(children.toArray(new Predicate[0]));
    });

    Assert.assertEquals(expected, queryCriteria.toString());
  }
}
