package com.example.compose.repository;

import com.example.compose.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

// Spring Data JPA generates all SQL automatically — no boilerplate needed.
// Equivalent to: db.query('SELECT COUNT(*) FROM visits WHERE path = $1', [path])
public interface VisitRepository extends JpaRepository<Visit, Long> {
    long countByPath(String path);   // SELECT COUNT(*) FROM visits WHERE path = ?
}
