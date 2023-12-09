package org.luncert.filtersquery.jpaimpl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILogHeaderRepo extends JpaRepository<LogHeader, String> {
}
