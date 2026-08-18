package com.example.compose.model;

import jakarta.persistence.*;
import java.time.Instant;

// JPA Entity — maps to the "visits" table in PostgreSQL
// Equivalent to the 'visits' table created in db/init.sql
@Entity
@Table(name = "visits")
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String path;

    @Column(name = "visited_at")
    private Instant visitedAt = Instant.now();

    public Visit() {}

    public Visit(String path) {
        this.path = path;
    }

    public Long getId()             { return id; }
    public String getPath()         { return path; }
    public Instant getVisitedAt()   { return visitedAt; }
}
