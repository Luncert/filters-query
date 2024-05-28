package org.luncert.filtersquery.jpaimpl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITagRepo extends JpaRepository<Tag, Long> {
}
