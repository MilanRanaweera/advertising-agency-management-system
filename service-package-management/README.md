# Member 2 — Services and packages

Frontend: `frontend/catalog.component.ts` and its HTML template.

Backend: `Offering`, `OfferingRepository`, `CatalogService`, `CatalogController`.

Explain CRUD and the state transitions DRAFT → REVIEW → PUBLISHED or CHANGES. Only published offerings are returned to guests. Services staff edit drafts; Marketing approves publication. A Promotion in this implementation is a fixed-price special offer, not a percentage-discount campaign.

SQL: `SELECT * FROM offering WHERE status = 'PUBLISHED';`

See `database/axiom-db.sql` for a complete simple offering CRUD example.

Marketing Managers can delete services/packages in any state, including published offerings. Service Staff retain draft/returned-record deletion only. Existing quotation line/price snapshots remain intact when an offering is deleted.
