# Payment and invoice management

Project Manager confirms completion after the final original is uploaded and designer progress reaches 100%. Completion creates a billing draft with customer name, email, project and quotation amount.

Finance staff fill in billing name/address, edit or delete draft/returned records, then Approve for invoice. Finance Manager reviews details, can return corrections, then Generate invoice and Send invoice. Customers see only sent/paid invoices and choose Demo card or Demo bank transfer. Successful simulated payment creates a receipt, emails it through the configured mail transport, and unlocks originals. Both finance roles and the customer can download the receipt.

States: DRAFT -> REVIEW -> GENERATED -> SENT -> PAID; returned records use CHANGES. Only finance staff can edit/delete DRAFT, CHANGES or legacy UNPAID records. A deleted draft can be recreated for its completed project. Amounts come from the approved quotation and cannot be changed in billing.

Payments are simulated; no money is transferred. Default mail mode stores receipts in the local outbox. Actual email delivery requires MAIL_ENABLED=true and SMTP configuration. Legacy payments retain their references and migrate to PAID so customers cannot be charged again; historic receipts may lack method/date metadata.
