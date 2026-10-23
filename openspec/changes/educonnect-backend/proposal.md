# Proposal: EduConnect Backend (Scala 3 + ZIO 2)

## Problem

In technical higher education (specifically within evening programs such as Instituto Técnico Nacional de Comercio "Federico Álvarez Plata"), communication between instructors and students frequently encounters friction:
- Critical academic notices get lost in informal messaging channels (e.g., WhatsApp groups).
- Students lack structured guidance and educational resources on effective and assertive academic communication.
- Feedback loops on coursework and doubts are asynchronous, disorganized, and unmeasured.
- There is no unified system to evaluate comprehension of communication principles or track formal announcements.

Furthermore, the academic contest for **Diseño y Programación Web III (2026)** establishes strict evaluation criteria (100 points total) requiring:
1. **Home & Presentation section**: Introduction to the theme, objective, key messaging, and direct navigation.
2. **Educational Content section**: Organized educational guides, articles, and recommendations on effective communication.
3. **Interactive Quiz**: Questions and options retrieved from a relational database, answer recording, dynamic score calculation, and personalized feedback.
4. **Developers section**: Team presentation, project mission, technology justification, and credits.

## Proposed Solution

**EduConnect** is a reactive, resilient backend API written in **Scala 3** leveraging the **ZIO 2** ecosystem, **PostgreSQL**, and **sttp Tapir** with integrated **Swagger UI / OpenAPI 3.0**. 

The backend serves as the core engine for an **Angular + Tailwind CSS** frontend, fulfilling all contest requirements while providing a practical, scalable communication hub for teachers and students:
- **Educational Knowledge Base**: REST endpoints serving categorized articles, communication tips, and multimedia references.
- **Interactive Quiz Engine**: Database-backed evaluation on effective communication styles, scoring logic, and student attempt histories.
- **Teacher-Student Communication Hub**: Formal announcement feed with priority flags (Urgent, Normal, Informational) and student inquiry/Q&A threads.
- **Developer & Project Metadata**: Transparent endpoints detailing system architecture, team credits, and technology rationales.
- **Swagger / OpenAPI Documentation**: Auto-generated interactive documentation at `/docs` and `/swagger` via Tapir, allowing live testing and schema export for Angular clients.

## Scope & Impact

### In Scope
- **RESTful Endpoints with Tapir & ZIO HTTP**:
  - `GET /api/v1/home/summary` (Home overview and system counters)
  - `GET /api/v1/content/articles`, `GET /api/v1/content/articles/{slug}`, `GET /api/v1/content/categories`
  - `GET /api/v1/quiz/questions`, `POST /api/v1/quiz/submit`, `GET /api/v1/quiz/attempts/{id}`
  - `GET /api/v1/announcements`, `POST /api/v1/announcements`
  - `GET /api/v1/developers`
  - `GET /docs` (Swagger UI) & `GET /docs/openapi.json` (OpenAPI specification)
- **Data Persistence**:
  - PostgreSQL relational database schema managed through Flyway migrations.
  - Type-safe queries using ZIO Quill (`zio-quill-jdbc-postgres`).
- **Configuration & Middleware**:
  - Cross-Origin Resource Sharing (CORS) configured for Angular frontend consumption.
  - Structured environment configuration with `zio-config`.
  - Comprehensive JSON encoding/decoding with `zio-json`.

### Out of Scope (Non-Goals)
- Full LMS features (e.g., grading book, assignments submission, video conferencing).
- Complex third-party OAuth integrations (Google/GitHub login) for the initial MVP; basic role-based authentication or mock-authenticated contexts will be prioritized to guarantee delivery.
- Direct frontend rendering in Scala (Angular + Tailwind CSS will be developed as a separate client application).

## Success Metrics

1. **Evaluation Compliance**: 100% adherence to the mandatory contest sections (Home, Educational Content, Interactive Quiz, Developers).
2. **Interactive API Discovery**: Functional Swagger UI available out-of-the-box at `/docs`.
3. **Robustness & Type Safety**: Zero unhandled exceptions in endpoint handlers, enforced by ZIO's strongly typed error channel `ZIO[R, E, A]`.
4. **Reproducible Environment**: Database migrations and service launch reproducible with a single `sbt run` and Docker Compose for PostgreSQL.
