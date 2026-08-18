-- Runs automatically when the postgres container starts for the first time.
-- Spring Boot JPA with ddl-auto=update will also create this table,
-- but init.sql ensures it exists before the app starts (avoids race conditions).

CREATE TABLE IF NOT EXISTS visits (
  id         BIGSERIAL    PRIMARY KEY,
  path       TEXT         NOT NULL,
  visited_at TIMESTAMPTZ  DEFAULT NOW()
);
