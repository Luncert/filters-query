package org.luncert.filtersquery.jpaimpl.repo;

import org.luncert.filtersquery.jpaimpl.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITagRepo extends JpaRepository<Tag, Long> {
}
