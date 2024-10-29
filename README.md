# filters-query

filters-query can translate your query string to JPA criteria, Lucene query and Java predicates, which empowers you build complex query API fast.

## Syntax

```
filter by <criterias> [offset <number>] [limit <number>] [sort by <orders>]
```

### 1.Criterias

filters-query supports following operators:

- between: ``exampleField between [,130]``
- empty, not empty: ``exampleField = empty``, ``exampleField != empty``
- starts with: ``exampleField startsWith "prefix"``
- ends with: ``exampleField endsWith "suffix"``
- like: ``exampleField like "asd"``
- equal, not equal: ``=``, ``!=``
- gt, get: ``>``, ``>=``
- lt, let: ``<``, ``<=``
- in: ``exampleField in ("Value1", "Value2")``

data types:

- number: ``130``, ``130.22``
- boolean: ``true``, ``false``
- string: ``"asd"``
- null: ``null``

bool expression: ``a = 123 and (b like "value" or c in [1,3,8])``

### Pagination

You can use limit and offset to do page query, like: ``filter by id > 10 offset 20 limit 10``, this query means requesting the page 2 (index from 0).

### Sorts

Examples:
- sort by id desc: ``sort by id desc``
- sort by multiple fields: ``filter by () sort by id desc, name asc``

## Integrate with JPA

Maven Dep:

```xml
<dependency>
  <groupId>org.luncert.filters-query</groupId>
  <version>1.0-SNAPSHOT</version>
  <artifactId>jpa-impl</artifactId>
</dependency>
```

Define entity according to JPA requirements:
```java
@Entity
class TestEntity {

  @Id
  private Long id;

  private String name;
}
```

Impl search engine:
```java
import jakarta.persistence.EntityManager;
import org.luncert.filtersquery.jpaimpl.JpaFiltersQueryEngine;
import org.luncert.filtersquery.jpaimpl.model.LogHeader;
import org.springframework.stereotype.Component;

@Component
public class TestEntitySearchImpl extends JpaFiltersQueryEngine<TestEntity> {

  public LogHeaderSearchImpl(EntityManager em) {
    super(em);
  }
}
```

Process query:
```java
searchEngine.searchPages("filter by id < 10 offset 5 limit 5");
```

## Translate query to predicates

Maven Dep:

```xml
<dependency>
  <groupId>org.luncert.filters-query</groupId>
  <version>1.0-SNAPSHOT</version>
  <artifactId>predicate-impl</artifactId>
</dependency>
```

Define model:
```java
class TestModel {

  private Long id;

  private String name;
}
```

Usage:
```java
List<TestModel> input = ...;
Predicate<TestModel> predicate = PredicateFiltersQueryEngine
  .buildQuery(criteria, TestModel.class);
List<TestModel> filtered = data.stream().filter(predicate);
```