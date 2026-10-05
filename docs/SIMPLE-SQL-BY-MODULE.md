# Simple SQL for your viva

Use a disposable practice database and replace example ID `1` with a real ID. The API is the normal way to change application data: these snippets demonstrate SQL, not permission or approval enforcement.

## 1. Users

```sql
SELECT id, name, email, role FROM app_user;
UPDATE app_user SET name = 'Updated name' WHERE id = 1;
UPDATE app_user SET active = false WHERE id = 1;
```

Create accounts through the API to hash passwords. `active = false` is soft deletion, preserving linked quotations and projects.

## 2. Services and packages

```sql
INSERT INTO offering (version, name, kind, description, deliverables, price, status)
VALUES (0, 'Poster design', 'Service', 'A poster', 'One PDF', 10000, 'DRAFT');
SELECT * FROM offering;
UPDATE offering SET price = 12000 WHERE name = 'Poster design';
DELETE FROM offering WHERE name = 'Poster design' AND status = 'DRAFT';
```

## 3. Quotations

```sql
SELECT * FROM quotation WHERE customer_id = 1;
UPDATE quotation SET requirements = 'Two logo concepts' WHERE id = 1 AND status = 'REQUESTED';
DELETE FROM quotation WHERE id = 1 AND status = 'REQUESTED';
```

Creation is through the API so it calculates prices and saves the selected lines. Do not change a sent quotation's total with raw SQL.

## 4. Projects

```sql
SELECT * FROM project;
UPDATE project SET progress = 25 WHERE id = 1;
SELECT * FROM assignment WHERE project_id = 1;
DELETE FROM project WHERE id = 1 AND status = 'PLANNED';
```

## 5. Invoices

```sql
SELECT * FROM invoice WHERE customer_id = 1;
UPDATE invoice SET billing_address = 'Colombo' WHERE id = 1 AND status = 'UNPAID';
DELETE FROM invoice WHERE id = 1 AND status = 'UNPAID';
```

Never mark an invoice paid with SQL in a real system. The application must verify the payment provider. The supplied local gateway is a labelled sandbox.

## 6. Communications

```sql
INSERT INTO conversation (version, customer_id, subject, message, kind, status)
VALUES (0, 1, 'Design question', 'Please contact me', 'MESSAGE', 'OPEN');
SELECT * FROM conversation;
UPDATE conversation SET reply = 'We will contact you' WHERE id = 1;
DELETE FROM conversation WHERE id = 1 AND status = 'OPEN';
```

## Key ideas

- `SELECT`: read rows.
- `INSERT`: create a row.
- `UPDATE ... WHERE`: edit only selected rows.
- `DELETE ... WHERE`: remove only selected rows.
- `PRIMARY KEY`: unique record identity.
- `FOREIGN KEY`: prevents linking to a missing user/project/quotation.
- `UNIQUE`: prevents duplicate emails and duplicate invoices for one project.
- Avoid running UPDATE or DELETE without WHERE during your viva.
