# Library Management Backend
Java 17 + Spring Boot 3.3 + Spring Security JWT + PostgreSQL + Flyway.

## IntelliJ
1. Create PostgreSQL database `library_db` or use Heroku PostgreSQL environment variables.
2. Open `backend/pom.xml` in IntelliJ.
3. Set Java SDK to 17.
4. Run `LibraryApplication`.

Local DB defaults: postgres/postgres on localhost:5432/library_db.

Demo accounts:
- admin@library.local / admin123
- manager@library.local / manager123
- user@library.local / user123

Production: change JWT_SECRET and passwords. AI/Email/SMS are provider abstractions; current implementation is mock/log based so no paid third party is required to run locally.
