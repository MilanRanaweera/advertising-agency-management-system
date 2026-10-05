# AXIOM — Six-module full-stack student project

Angular 20 + Java 17 + Spring Boot 3.5 + PostgreSQL 16.

This ZIP contains a database-connected local application, not just HTML screens. Each team member has a separate frontend/backend section. All six sections run together as one application so approvals and records can move between departments.

**Updating your working installation? Read [UPGRADE.md](UPGRADE.md) first. No database recreation is needed.**

## Optional start: Docker Desktop

1. Install and start Docker Desktop (or Docker Engine with Compose).
2. Extract this ZIP and open a terminal in `advertising-agency-management-system` (the folder containing `compose.yaml`).
3. Run:

```sh
docker compose up --build
```

The first run downloads dependencies and can take several minutes. Wait for the Spring `Started Application` message.

- Application: http://localhost:4200
- Captured test emails: http://localhost:8025
- API: http://localhost:8080/api
- Database: localhost:5432, database `axiom-db`, username `axiom`, password `axiom@2026`

If the frontend opens before the API is ready, refresh it. If you see port conflicts, stop the other process or change the host port on the left of the port mapping in `compose.yaml`.

Stop without deleting your data:

```sh
docker compose down
```

Named volumes preserve PostgreSQL records and uploaded files. **Do not add `-v` unless you intentionally want to erase the project database and uploaded files.**

## Demo sign-ins

Administrator login: **`admin`** / **`admin@2026`**. All other demo accounts below use **`AxiomDemo123!`**. These are local demonstration accounts only. The admin account retains `admin@axiom.test` as its stored email; `admin` is its sign-in username.

| Account | Role / responsibility |
|---|---|
| admin | User and access management (password: admin@2026) |
| service_staff@axiom.test | Create services, packages, promotional offerings |
| marketing_manager@axiom.test | Review and publish offerings |
| quotation_staff@axiom.test | Generate and revise quotations |
| finance_manager@axiom.test | Approve and send quotations and invoices |
| project_manager@axiom.test | Start projects and assign designers |
| designer@axiom.test | Progress, prototype and original uploads |
| finance_staff@axiom.test | Payment requests and billing corrections |
| communication_staff@axiom.test | Customer communication |
| customer_relations_officer@axiom.test | Consultation confirmations |
| customer@axiom.test | Briefs, reviews, payments and downloads |

Sign out before switching roles. Reloading the browser signs you out: credentials stay in memory, not browser storage. Database records remain saved. You can create additional designers and customers from the admin account.

## Manual setup (without building Docker application images)

### Renaming a much older database (skip if yours is already axiom-db)

Stop the old backend. Use `database/update-existing-database.sql` from a pgAdmin connection to the `postgres` database to change the database-user password and rename the old `axiom` database to `axiom-db`, preserving records. Run each command separately. Skip the rename if it is already named `axiom-db`. Existing Docker volumes also retain the previous credentials/database name; editing Compose alone does not migrate them. Do not delete volumes to solve this.

On startup with demo mode enabled, the original admin demo password is migrated to `admin@2026` only if it is still `AxiomDemo123!`. Custom passwords are not reset. Sign in with username `admin`. Other staff/customer demo passwords are unchanged.

`database/axiom-db.sql` is the renamed **practice CRUD query file**, not the database creation script. For first-time setup, use `database/create-database.sql` instead.

### First-time setup

Install Java 17, Maven 3.9+, Node 22.12+ (22 LTS recommended), and PostgreSQL 16+. The Angular compatibility table is at https://angular.dev/reference/versions and Spring requirements are at https://docs.spring.io/spring-boot/3.5/system-requirements.html.

You may start only the infrastructure using:

```sh
docker compose up -d db mailpit
```

Or create the database manually with `database/create-database.sql` using PostgreSQL's `psql` or pgAdmin as an administrator. Run `CREATE DATABASE` outside a transaction. If using different credentials, set `DB_URL`, `DB_USER`, and `DB_PASSWORD` before starting the backend.

Terminal 1, from the project root:

```sh
mvn spring-boot:run
```

Terminal 2, from the same project root:

```sh
npm ci
npm start
```

Open http://localhost:4200. Angular's proxy sends `/api` calls to Spring on port 8080. Do not open `index.html` directly.

When running manually, email defaults to the database outbox. To capture actual SMTP messages in Mailpit, set `MAIL_ENABLED=true`, `MAIL_HOST=localhost`, and `MAIL_PORT=1025`. Compose sets these automatically.

## Folder ownership

```text
user-management/                 frontend/ + backend/
service-package-management/      frontend/ + backend/
quotation-management/            frontend/ + backend/
project-management/              frontend/ + backend/
payment-invoice-management/      frontend/ + backend/
communication-management/        frontend/ + backend/
application/                     shared Angular shell + Spring infrastructure
database/                        schema and simple SQL examples
docs/                            viva guide, API reference, test checklist
design-reference/                previous frontend-only visual prototype
```

Open the root `pom.xml` in IntelliJ IDEA as a Maven project. `build-helper-maven-plugin` adds the six backend source folders to one Spring application. Open the entire root folder in VS Code for Angular. Do not run `npm install` or Maven separately inside each module.

`application/frontend/src/workbench.ts` provides common CRUD/form behavior. Each module owns its component, configuration and HTML template. Backend controllers call business services, which use Spring Data repositories and PostgreSQL entities.

## Included working scope

- Database-backed registration, BCrypt password hashes and server-side role/record checks.
- Admin account creation, role changes and permanent database deletion; accounts linked to business records cannot be deleted and can be deactivated using Edit instead.
- Offering CRUD and marketing approval before public publication.
- Customer selection cart, requirements, server-calculated quotation totals and finance feedback loops.
- Quotation staff pricing, finance approval, branded PDF downloads and PDF email attachments via configured SMTP.
- In-app customer acceptance, accepted-quotation project creation and multiple designer assignment with capacity checks.
- Progress updates, file uploads, customer prototype feedback and director-only internal feedback.
- Project completion creates a billing draft; finance staff approve details, finance managers generate/send invoices, and customers pay by demo card or bank transfer and receive receipts.
- Original-file downloads blocked by the **server** until prototype approval and full payment with a sent approved invoice.
- Conversations, replies, consultation requests and CUSTOMER_RELATIONS_OFFICER confirmation.
- Dark responsive Angular screens, reusable colorful assets and the interactive Earth.

## Important scope and production limitations

This is a runnable educational implementation, not a production deployment or a pixel-for-pixel port of every previous prototype screen.

- **Payments are simulated.** `SandboxGateway` generates a sandbox reference and never charges a card. `PaymentGateway` is the interface for a future Stripe implementation. Real Stripe checkout, signed webhooks, refunds, disputes and partial payments are not implemented.
- **Compose emails go to Mailpit**, not real customer inboxes. Manual mode stores messages in the outbox unless SMTP is enabled. Real SMTP requires your host credentials and verified sending address. No incoming-email parsing is implemented; customers accept quotations inside the application.
- Quotations are server-generated **PDFs**, available only after approval. Invoices remain printable HTML. Configure `STUDIO_NAME`, `STUDIO_ADDRESS`, `STUDIO_EMAIL`, and `STUDIO_PHONE` for quotations; invoice contact details remain in `Documents.java`.
- Each account has one assigned role in this implementation. Finance Manager has both quotation and invoice responsibilities. Multi-role assignment is not implemented.
- Promotion records are fixed-price promotional offerings. Date-based percentage-discount campaigns are not implemented here.
- Communications preserves a customer request and a chronological reply history. Customers can reply after a staff response until staff mark the conversation Solved; updates use periodic refresh, not real-time chat. Internal company messaging, an AI chatbot and password-reset email are not implemented.
- This local edition uses HTTP Basic credentials held only in Angular memory. TLS, production session/token hardening, CSRF policy review, login throttling, password reset, email verification, malware scanning, backups and a full audit trail are required before public deployment.
- Sample accounts are seeded only with `AXIOM_DEMO=true`. Do not publish this configuration. Setting it to false disables the sandbox and seeding but does not delete existing demo users; replace/disable them explicitly.
- Uploads are limited to 10 MB. Files are private API downloads; file MIME verification and malware scanning need strengthening for production.
- All actions are role-controlled; the administrator manages access, not other departments' approvals.

## SQL and tests

Spring runs the idempotent `schema.sql` at startup and validates it against the entities. The canonical readable copy is `database/schema.sql`; the runtime copy is `application/backend/src/main/resources/schema.sql`. Keep both synchronized if you change the schema. Development sample accounts are created in `DemoData.java` so passwords are hashed rather than stored as plaintext SQL.

```sh
mvn test
npm run build
```

The backend integration tests use H2 in PostgreSQL compatibility mode for fast local tests. Run the manual checklist against actual PostgreSQL before your viva. See `docs/VERIFICATION.md` for what was checked when this ZIP was prepared.

Start with `docs/DEMO-WALKTHROUGH.md` for a complete role-by-role demonstration.
