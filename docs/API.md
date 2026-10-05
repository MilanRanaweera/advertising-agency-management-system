# API quick reference

Base URL: `http://localhost:8080/api`. Authenticated requests use HTTP Basic in the local edition. JSON bodies use `Content-Type: application/json`. The Angular development proxy and Docker Nginx proxy avoid cross-origin requests.

| Section | Endpoints |
|---|---|
| Users | `POST /auth/register`, `GET /auth/me`, `GET/POST /users`, `PUT/DELETE /users/{id}`, `GET /users/designers` |
| Catalogue | `GET /catalog/public`, `GET/POST /catalog`, `PUT/DELETE /catalog/{id}`, `POST /catalog/{id}/submit`, `/approve`, `/return` |
| Quotations | `GET/POST /quotes`, `PUT/DELETE /quotes/{id}`, `POST /quotes/{id}/generate`, `/approve`, `/return`, `/send`, `/accept`, `GET /quotes/{id}/document` |
| Projects | `GET/POST /projects`, `DELETE /projects/{id}`, `GET/PUT /projects/{id}/team`, `PUT /projects/{id}/progress`, `POST /projects/{id}/review`, `GET /projects/{id}/feedback` |
| Files | `GET/POST /projects/{id}/assets`, `GET /projects/assets/{id}/download` |
| Invoices | `GET/POST /invoices`, `PUT/DELETE /invoices/{id}`, `POST /invoices/{id}/pay`, `/resubmit`, `/approve`, `/return`, `/send`, `GET /invoices/{id}/document` |
| Communication | `GET/POST /communications`, `PUT/DELETE /communications/{id}`, `POST /communications/{id}/reply`, `/confirm`, `/close`, `GET /communications/outbox` |

## Sample requests

Customer brief:

```json
{"title":"New brand","requirements":"Logo and social launch","items":[{"offeringId":1,"quantity":1}]}
```

Project creation (director):

```json
{"quotationId":1}
```

Team assignment (director):

```json
{"designers":[7,8]}
```

Use actual designer IDs from `GET /users/designers`, not these examples.

Feedback return:

```json
{"feedback":"Please correct the billing address"}
```

Prototype review:

```json
{"approved":false,"message":"Please revise the heading"}
```

File upload is multipart with `file` and `kind` (`PROTOTYPE` or `ORIGINAL`). Quotation documents return application/pdf after approval; invoice documents return printable HTML; file endpoints return attachments. State violations return 400, role/ownership violations return 403, and conflicting/referenced records return 409.
