# Individual module ownership and OOP

| Member | Folder | Main responsibility | Tables |
|---|---|---|---|
| 1 | user-management | Authentication, registration, roles, users | app_user |
| 2 | service-package-management | Offering CRUD and publication approval | offering |
| 3 | quotation-management | Brief pricing, generation, finance decisions | quotation |
| 4 | project-management | Assignment, progress, files, prototype feedback | project, assignment, asset, project_feedback |
| 5 | payment-invoice-management | Payment abstraction, invoice workflow, billing | invoice |
| 6 | communication-management | Customer requests, replies, consultations | conversation |

Shared infrastructure (`application`) includes the application entry point, API client, security-independent utilities, mail outbox, document rendering and common UI behavior. Assign shared-file changes explicitly in your team to avoid merge conflicts.

## OOP concepts with actual examples

- **Encapsulation:** service dependencies are private final fields initialized through constructors. Business changes go through service methods instead of directly exposing repository operations as public endpoints. JPA entities use public fields for a deliberately simple teaching model; do not claim those entities demonstrate private-field encapsulation.
- **Abstraction:** `PaymentGateway` defines the payment contract. The invoice service does not need to know how an external provider works.
- **Polymorphism:** `InvoiceService` depends on `PaymentGateway`, while Spring injects `SandboxGateway`. A future verified Stripe implementation can satisfy the same interface.
- **Inheritance:** entities extend `BaseEntity` for ID, version and creation time. Each Angular module component extends `Workbench` to reuse form, loading and CRUD behavior.
- **Composition/dependency injection:** a controller has a service; a service has repositories and an access checker. Components compose the shared shell rather than duplicating authentication.
- **Immutable request DTOs:** Java records such as `Brief`, `Line`, `Team` and `Review` describe incoming requests.
- **Separation of concerns:** controller = HTTP mapping; service = business rules; repository = persistence; entity = stored state; Angular component/template = interaction and display.

## Be ready to explain

1. Follow one click from Angular template → `Api` → REST controller → service → repository → PostgreSQL.
2. Show an INSERT, SELECT, UPDATE and DELETE relevant to your own module.
3. Explain why the server checks roles even when Angular hides a button.
4. Explain primary keys, foreign keys and the unique project/quotation and invoice/project relationships.
5. Show how optimistic locking (`@Version`) rejects conflicting record updates.
6. Explain how transactions roll back multi-step state changes.
7. Identify what is a local simulation: sandbox payment and test email inbox. Do not describe these as live Stripe or real customer delivery.
8. Explain the original-file rule in `AssetService.allowed`: approved prototype + enough paid, sent invoices. The frontend label alone is not security.

This project is intentionally a modular monolith, not six microservices. One server and one database keep the individual sections understandable while supporting cross-module workflows.
