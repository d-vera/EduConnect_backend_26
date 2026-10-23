# Tasks: EduConnect Backend Implementation

## Phase 1: Project Setup & Build Infrastructure
- [x] 1.1 Initialize `build.sbt` with Scala 3.3.3 and dependencies (`zio`, `zio-http`, `tapir-zio`, `tapir-swagger-ui-bundle`, `tapir-json-zio`, `zio-quill-jdbc-postgres`, `flyway-core`, `hikariCP`, `postgresql`, `logback-classic`).
- [x] 1.2 Create `project/build.properties` and configure sbt version.
- [x] 1.3 Add `docker-compose.yml` for local PostgreSQL 16 database and pgAdmin / environment variables.
- [x] 1.4 Configure `src/main/resources/application.conf` for database credentials, server port (8080), and CORS origins.

## Phase 2: Database Schema & Flyway Migrations
- [x] 2.1 Implement `V1__init_schema.sql` defining `categories`, `articles`, `quiz_questions`, `quiz_options`, `quiz_attempts`, and `announcements`.
- [x] 2.2 Implement `V2__seed_initial_data.sql` with rich educational content on Effective Communication (assertiveness, active listening, academic feedback) and the interactive quiz questions with answer choices.
- [x] 2.3 Create database migration runner utility with Flyway to automatically apply migrations on startup.

## Phase 3: Domain Models & Data Access Layer (Repositories)
- [x] 3.1 Define domain case classes and `zio-json` codecs in `domain/models.scala` (`Article`, `Category`, `QuizQuestion`, `QuizOption`, `QuizAttempt`, `Announcement`, `DeveloperInfo`).
- [x] 3.2 Define strongly-typed error hierarchy in `domain/errors.scala` (`NotFoundError`, `ValidationError`, `DatabaseError`).
- [x] 3.3 Implement `ArticleRepository` with ZIO Quill queries (`listAll`, `findBySlug`, `listByCategory`).
- [x] 3.4 Implement `QuizRepository` with queries to fetch active questions, retrieve question options, and record student attempts.
- [x] 3.5 Implement `AnnouncementRepository` with queries to insert and list announcements sorted by priority.

## Phase 4: Business Logic Services
- [x] 4.1 Implement `ContentService` with caching/slug validation for articles and categories.
- [x] 4.2 Implement `QuizService` with scoring algorithm, answer correctness evaluation, score percentage calculation, and personality feedback assignment.
- [x] 4.3 Implement `AnnouncementService` with validation for priority levels and creation timestamps.
- [x] 4.4 Implement `MetadataService` providing the Home overview metrics and the Developers & Technology Stack credits.

## Phase 5: Tapir Endpoints & Swagger Documentation
- [x] 5.1 Define Tapir endpoints in `api/Endpoints.scala` with full OpenAPI metadata, summaries, request/response bodies, and error channels.
- [x] 5.2 Configure Swagger UI bundle via `SwaggerInterpreter` mounted at `/docs` and `/docs/openapi.json`.
- [x] 5.3 Bind Tapir endpoints to ZIO HTTP routes using `ZioHttpServerInterpreter`.
- [x] 5.4 Configure CORS middleware in ZIO HTTP to permit cross-origin requests from Angular frontend (`http://localhost:4200`).

## Phase 6: Server Assembly & End-to-End Verification
- [x] 6.1 Wire dependency graph using `ZLayer.make` in `Main.scala` and launch the HTTP server.
- [x] 6.2 Verify Swagger UI accessibility and interactively test all endpoints at `http://localhost:8080/docs`.
- [x] 6.3 Test quiz submission flow: submit sample answers, verify score calculation, and confirm persistence in PostgreSQL.
- [x] 6.4 Validate 100% compliance with contest evaluation sections (Home, Educational Content, Interactive Quiz, Developers).
