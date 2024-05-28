package org.luncert.filtersquery.jpaimpl.repo;

import org.luncert.filtersquery.jpaimpl.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILabelRepo extends JpaRepository<Label, Label.ID> {
}
