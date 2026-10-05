-- OPTIONAL migration if you already created the old database named axiom.
-- Stop the backend first. Connect pgAdmin Query Tool to the postgres database
-- as your PostgreSQL administrator, NOT to the axiom database.
-- Close other connections to axiom. Run each command separately with auto-commit.
-- Do not run CREATE DATABASE again if you want to preserve the existing data.
ALTER USER axiom WITH PASSWORD 'axiom@2026';
ALTER DATABASE axiom RENAME TO "axiom-db";
-- If your database is already axiom-db, skip the second statement.
-- The rename preserves its records. Restart the backend using the updated configuration.
