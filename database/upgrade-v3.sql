-- Safe upgrade: preserves accounts, quotations, projects and payments.
ALTER TABLE app_user ADD COLUMN IF NOT EXISTS deleted BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE quotation ADD COLUMN IF NOT EXISTS additional_amount NUMERIC(38,2) DEFAULT 0;
ALTER TABLE quotation ADD COLUMN IF NOT EXISTS additional_description VARCHAR(10000);
UPDATE app_user SET role = 'SERVICE_STAFF' WHERE role = 'SERVICES';
UPDATE app_user SET role = 'MARKETING_MANAGER' WHERE role = 'MARKETING';
UPDATE app_user SET role = 'QUOTATION_STAFF' WHERE role = 'QUOTES';
UPDATE app_user SET role = 'FINANCE_MANAGER' WHERE role IN ('FINANCE_QUOTES', 'FINANCE_PAYMENTS');
UPDATE app_user SET role = 'PROJECT_MANAGER' WHERE role = 'DIRECTOR';
UPDATE app_user SET role = 'FINANCE_STAFF' WHERE role = 'PAYMENTS';
UPDATE app_user SET role = 'COMMUNICATION_STAFF' WHERE role = 'COMMUNICATIONS';
UPDATE app_user SET role = 'CUSTOMER_RELATIONS_OFFICER' WHERE role = 'CRO';
UPDATE project_feedback SET author_role = 'PROJECT_MANAGER' WHERE author_role = 'DIRECTOR';
