# Member 6 — Communications

Frontend: `frontend/communications.component.ts` and its HTML template.

Backend: `Conversation`, `ConversationRepository`, `CommunicationService`, `CommunicationController`.

Explain customer ownership, request/reply records, future consultation times in Asia/Colombo, and CUSTOMER_RELATIONS_OFFICER-only confirmation. The outbox endpoint exposes a customer's own mail and authorized staff's delivery log. This edition is not a live-chat or AI-chatbot implementation.

SQL: `SELECT * FROM conversation WHERE kind = 'CONSULTATION';`

Shared SMTP delivery uses Mailpit in Docker. Port 8025 displays captured emails; it does not send them to the internet.

Customers may reply after the first staff response while a conversation is OPEN or CONFIRMED. Replies retain author, role, time and text in conversation_reply; existing single replies remain visible. Communication Staff and Customer Relations Officers can mark a conversation SOLVED, which closes replies for everyone. Communication Staff cannot delete conversations, enforced in the API and UI. Customer ownership is enforced for replies as well as reads.
