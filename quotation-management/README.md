# Member 3 — Quotations

Frontend: `frontend/quotes.component.ts` and its HTML template.

Backend: `Quotation`, `QuotationRepository`, `QuotationService`, `QuotationController`.

Explain immutable `Brief`/`Line` DTO records, server-side totals, price snapshots in the quotation lines, and the review/return loop. The client cannot choose the final total. Finance sends only approved quotations. Acceptance is in-app, not parsed from email.

SQL: `SELECT * FROM quotation WHERE status = 'REVIEW';`

Document and email infrastructure is shared in `application/backend/.../core`.
