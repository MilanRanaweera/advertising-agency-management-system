# Complete demonstration

Use separate browser profiles or sign out and back in between roles. Refresh each workspace after another role changes a record. Record the numeric IDs shown in the cards; project and invoice creation use those IDs.

1. **Services:** create an offering, edit the draft, then submit it. Marketing approves it. Confirm the offering appears publicly. Marketing can return it with feedback; Services edits and resubmits it.
2. **Customer:** select offerings in Services & packages. Open Quotations → Create new, enter a title and requirements, and save. The backend calculates the price from the published offerings; the browser cannot set the total.
3. **Quotation employee:** generate the quotation. **Finance quotation executive:** return with feedback or approve; after approval, send it. Inspect the downloaded document and Mailpit on port 8025.
4. **Customer:** accept the sent quotation. **Director:** open Quotations to see the accepted record, then Projects → Create new using its quotation ID.
5. **Admin:** optionally create more DESIGNER accounts and set their capacities. **Director:** open the project, select one or more available designers and save. Workload includes all unfinished assigned projects.
6. **Designer:** open the assigned project, set progress and upload a PNG/JPEG prototype. **Customer:** download/review it, provide feedback and approve or request changes.
7. **Director:** review the customer's decision and add internal feedback. Approval is disallowed if the customer requested changes. A rejected prototype returns to the designer for another upload. Internal director comments are excluded from customer API responses.
8. **Designer:** after prototype approval, upload the ORIGINAL (PNG, JPEG, PDF or ZIP). The customer can see that a final file exists but cannot download it yet. Try the API directly: it also rejects the download.
9. **Payment employee:** create a payment request using the project ID. **Customer:** edit billing name/address, then select Pay (sandbox). No real money is used; an invoice enters finance review.
10. **Finance invoice executive:** return for corrections if needed. **Payment employee:** edit billing and resubmit. Finance approves, then sends the invoice. Approval alone is not sufficient to unlock originals; sending is required.
11. **Customer:** download the sent invoice and original file. Check the captured invoice email in Mailpit.
12. **Customer:** create a consultation request in Communications, selecting a future date/time in Sri Lanka. **CUSTOMER_RELATIONS_OFFICER:** confirm and reply. The customer sees the response.

## Demonstrating CRUD safely

- User section: create → list → edit role → delete. Deleted accounts disappear from the list and cannot sign in; referenced history remains. Designer capacity is managed by the Project Manager.
- Catalogue: create draft → view → edit → delete an unsubmitted draft.
- Quotation: submit brief → list/download → employee edits requirements → delete an unprocessed request.
- Project: create from accepted quotation → view → assign/update progress → delete a still-PLANNED project.
- Payment: create unpaid request → view → edit billing → delete only an unpaid request.
- Communication: create open message → view → edit → delete while open.

Processed financial records are not arbitrarily deleted. Explain the status rules and referential integrity during your viva.
