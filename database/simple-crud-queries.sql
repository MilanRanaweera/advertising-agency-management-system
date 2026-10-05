-- For learning in a disposable practice database. The real API enforces role and approval rules.
-- CREATE DATABASE axiom; -- Run separately as a PostgreSQL administrator if not using Docker.
-- READ
SELECT id, name, email, role, active FROM app_user;
SELECT * FROM offering WHERE status = 'PUBLISHED';
SELECT * FROM quotation WHERE status = 'REVIEW';
SELECT * FROM project ORDER BY id DESC;
SELECT * FROM invoice WHERE status = 'SENT';
SELECT * FROM conversation WHERE kind = 'CONSULTATION';
-- CREATE a draft offering
INSERT INTO offering (version, created_at, name, kind, description, deliverables, price, status)
VALUES (0, CURRENT_TIMESTAMP, 'Poster design', 'Service', 'A custom poster', 'One poster, two revisions', 15000, 'DRAFT');
-- UPDATE only the practice draft
UPDATE offering SET price = 18000 WHERE name = 'Poster design' AND status = 'DRAFT';
-- DELETE only the practice draft
DELETE FROM offering WHERE name = 'Poster design' AND status = 'DRAFT';
-- Simple join: project and customer
SELECT project.title, app_user.name FROM project JOIN app_user ON project.customer_id = app_user.id;
-- Simple total
SELECT SUM(amount) AS delivered_invoice_total FROM invoice WHERE status = 'SENT';
-- Staff workload
SELECT designer_id, COUNT(*) AS assigned_projects FROM assignment GROUP BY designer_id;
-- Accounts must be created with the API: it hashes passwords. Do not INSERT plaintext passwords.
-- Use module APIs for writes to quotations, invoices and projects to preserve approvals.
