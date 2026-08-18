-- Runs automatically when the postgres container starts for the first time
CREATE TABLE IF NOT EXISTS visits (
  id   SERIAL PRIMARY KEY,
  path TEXT NOT NULL,
  at   TIMESTAMPTZ DEFAULT NOW()
);
