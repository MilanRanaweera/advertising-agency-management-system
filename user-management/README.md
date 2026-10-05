# Member 1 — User and access management

Frontend: `frontend/users.component.ts` and its HTML template.

Backend: `AppUser`, `AppUserRepository`, `UserService`, `UserController`, `Access`, `SecurityConfig`.

Explain BCrypt hashing, the registration DTO, role checks, permanent account deletion with foreign-key protection for linked history, and why registration always assigns CUSTOMER even if a different role is submitted. Accounts linked to business records must be deactivated using Edit instead. Show private service dependencies as encapsulation. The role list includes all eleven roles.

SQL: `SELECT id, name, role FROM app_user;`

The root README contains startup commands; this folder is not a separate server.
