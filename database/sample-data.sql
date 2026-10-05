-- Optional catalogue-only sample. The app already seeds six offerings in demo mode.
-- Run after schema.sql. Do not use this to create plaintext-password accounts.
INSERT INTO offering (version, created_at, name, kind, description, deliverables, price, status)
SELECT 0, CURRENT_TIMESTAMP, 'Print design', 'Service', 'A print-ready brochure',
       'Design concepts, revisions, final PDF', 25000, 'DRAFT'
WHERE NOT EXISTS (SELECT 1 FROM offering WHERE name = 'Print design');
