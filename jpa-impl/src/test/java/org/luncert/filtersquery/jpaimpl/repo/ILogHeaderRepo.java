package org.luncert.filtersquery.jpaimpl.repo;

import org.luncert.filtersquery.jpaimpl.model.LogHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILogHeaderRepo extends JpaRepository<LogHeader, Long> {
}
