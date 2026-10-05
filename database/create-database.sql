-- Run these commands as a PostgreSQL administrator, once, outside a transaction.
-- Docker Compose already performs this setup; do not run it again with Compose.
CREATE USER axiom WITH PASSWORD 'axiom@2026';
CREATE DATABASE "axiom-db" OWNER axiom;
-- Connect to database axiom-db as user axiom before running schema.sql.
-- Only for a local student project. Use a private, strong password outside local testing.
