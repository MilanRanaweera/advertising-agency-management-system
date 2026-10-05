# Update your existing AXIOM project

This update keeps the six frontend/backend modules and existing PostgreSQL records.

1. Stop Spring Boot and Angular with Ctrl+C.
2. Back up your current project folder and database using pgAdmin Backup.
3. Extract this ZIP into a **new folder**. Open the folder containing `pom.xml` and `package.json`.
4. Copy the old project's `uploads` folder into the new project root if you have uploaded design files. Keep custom database/SMTP settings. Do not overwrite the new schema or source code with older files.
5. Start the backend using your working database connection. Your screenshots showed PostgreSQL port **5433**:

```powershell
mvn spring-boot:run "-Dspring-boot.run.arguments=--spring.datasource.url=jdbc:postgresql://localhost:5433/axiom-db --spring.datasource.username=axiom --spring.datasource.password=axiom@2026"
```

The new schema automatically adds columns and converts old roles. You do **not** need to recreate the database, rerun sample data, or paste practice CRUD queries. `database/upgrade-v3.sql` contains the same simple upgrade queries for review or manual execution in **axiom-db**. Running them again is safe.

6. In a second terminal, from the same project root:

```powershell
npm ci --fetch-retries=5 --fetch-retry-mintimeout=20000 --fetch-retry-maxtimeout=120000
npm start
```

This ZIP includes matching updated package.json and package-lock.json files. Use both together. Do not copy the old node_modules folder.

7. Open http://localhost:4200. Admin login remains `admin` / `admin@2026`. Existing custom emails and passwords remain unchanged. Sign out and back in after upgrading to load the new role.

## Roles

| Displayed role | Role code | New demo account |
|---|---|---|
| Admin | ADMIN | admin |
| Customer | CUSTOMER | customer@axiom.test |
| Quotation Staff | QUOTATION_STAFF | quotation_staff@axiom.test |
| Project Manager | PROJECT_MANAGER | project_manager@axiom.test |
| Designer | DESIGNER | designer@axiom.test |
| Marketing Manager | MARKETING_MANAGER | marketing_manager@axiom.test |
| Service & Package Staff | SERVICE_STAFF | service_staff@axiom.test |
| Finance Manager | FINANCE_MANAGER | finance_manager@axiom.test |
| Finance Staff | FINANCE_STAFF | finance_staff@axiom.test |
| Communication Staff | COMMUNICATION_STAFF | communication_staff@axiom.test |
| Customer Relations Officer | CUSTOMER_RELATIONS_OFFICER | customer_relations_officer@axiom.test |

Other demo accounts use `AxiomDemo123!`, seeded when AXIOM_DEMO=true. Older emails remain usable; their roles migrate without changing passwords. Both former finance roles become Finance Manager. Project Manager replaces Managing Director.

## Quotation workflow

1. Customer adds offerings in Services & packages. In Quotations → Create new, use +, −, or Remove to adjust the brief, then Request quotation.
2. Quotation Staff opens Edit, enters additional price and its reason if needed, saves, then selects Submit for finance approval.
3. Finance Manager approves or returns with feedback. Staff can revise and resubmit returned quotations.
4. After approval, Customer, Quotation Staff and Finance Manager can download the branded PDF. Earlier download requests are blocked by the server.
5. Finance Manager selects Send quotation to email the PDF when SMTP is configured. Local mode records the email in the outbox; it does not deliver to an external inbox.
6. Customer selects Accept quotation & start project and confirms. The button appears after approval, even before email delivery.
7. Project Manager opens Quotations. Accepted quotes show Start project & assign designers. Open the project to select available designers. Each quotation creates only one project.

Dashboards refresh every 15 seconds while visible, or immediately with Refresh.

## Other changes

- Full-width landing background, spaced authentication buttons, removed login demo text and dashboard technical footer.
- Admin Delete hides another account and blocks sign-in, preserving linked history. Self-deletion remains unavailable.
- Designer capacity is removed from the admin form. Only Project Manager sees capacity controls in the project's team view.
- Original-file downloads still require full payment and an approved, sent invoice.
- Configure STUDIO_NAME, STUDIO_ADDRESS, STUDIO_EMAIL and STUDIO_PHONE with real agency details before issuing quotations. Default contact details are samples.

## Invoice workflow update

Restart the backend and refresh the frontend after updating. Startup applies additive invoice metadata columns and preserves existing records. Existing invoices with payment references migrate to PAID; old unpaid records remain editable by finance staff. Already completed projects can have a billing draft created by finance staff. New final uploads use FINAL_REVIEW and require 100% designer progress and explicit Project Manager completion.

Invoices now follow finance staff billing approval, finance manager generation/delivery, then customer demo payment and receipt. Originals unlock only after payment. Receipt emails use the existing SMTP/local-outbox configuration.

## Conversation replies and catalogue permissions

Restart the backend and refresh the browser. Startup adds conversation_reply without removing existing requests or replies and renames CLOSED to SOLVED. Marketing Managers can delete published offerings. Communication Staff cannot delete messages; customers can reply after staff until staff mark the conversation Solved.

## Customer profiles and PDF billing documents

Restart your existing backend terminal (Ctrl+C, then `mvn spring-boot:run`) and refresh the frontend. Startup adds profile fields and invoice contact/deletion metadata without removing existing records. New registrations collect address, contact number, sex and date of birth; Admin can update these details for existing users. Only address/contact details are printed on quotations and billing PDFs. New billing drafts copy the customer profile address/contact.

Invoice and receipt download endpoints now return application/pdf. Outgoing invoice/receipt emails attach PDFs when SMTP is enabled. The local outbox retains the email text; PDFs remain downloadable from the invoice screen. Visa, Mastercard and bank transfer are demonstration choices, not a live payment integration.

Finance Manager deletion removes invoices from the finance list while retaining stored financial history and preventing another invoice for the same project. Customers keep access to paid receipts and unlocked files. Finance Staff retain draft-only deletion.
