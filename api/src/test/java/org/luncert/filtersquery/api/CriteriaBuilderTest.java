package org.luncert.filtersquery.api;

import static org.luncert.filtersquery.api.criteria.Reference.ref;
import static org.luncert.filtersquery.api.criteria.Predicate.*;
import static org.luncert.filtersquery.api.criteria.Value.literal;
import static org.luncert.filtersquery.api.criteria.Value.number;

import org.junit.Assert;
import org.junit.Test;
import org.luncert.filtersquery.api.criteria.CriteriaBuilder;

public class CriteriaBuilderTest {

  @Test
  public void test1() {
    var criteria = CriteriaBuilder.create()
        .filterBy(or(
            and(
                ref("a").between(number(0), number(100)),
                ref("b").like(literal("hi"))
            ),
            ref("c").gte(number(10))
        ))
        .sortBy(ref("a").asc(), ref("b").desc())
        .offset(10)
        .limit(10)
        .build();
    Assert.assertEquals("filter by ((a between (0, 100) and b like \"hi\") or c >= 10) sort by a asc, b desc offset 10 limit 10", criteria);
  }

  @Test
  public void test2() {
    var criteria = CriteriaBuilder.create()
        .filterBy(or(
            and(
                ref("a").lt(number(100)),
                ref("b").startsWith(literal("hi"))
            ),
            ref("c").lte(number(10))
        ))
        .offset(10)
        .limit(10)
        .build();
    Assert.assertEquals("filter by ((a < 100 and b startsWith \"hi\") or c <= 10) offset 10 limit 10", criteria);
  }

  @Test
  public void test3() {
    var criteria = CriteriaBuilder.create()
        .filterBy(or(
            and(
                ref("a").gt(number(0)),
                ref("b").endsWith(literal("hi"))
            ),
            ref("c").isEmpty()
        ))
        .build();
    Assert.assertEquals("filter by ((a > 0 and b endsWith \"hi\") or c = empty)", criteria);
  }
}
