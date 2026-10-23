# Design: EduConnect Backend Architecture

## Context & Architecture

EduConnect is built as a reactive, type-safe REST API using **Scala 3 (3.3.x LTS)** and the **ZIO 2** ecosystem. It powers an **Angular + Tailwind CSS** frontend and strictly adheres to the 2026 Web III contest requirements while providing practical communication tools for teachers and students.

### Architectural Layers (ZIO ZLayer Pattern)

The system adopts a Clean/Hexagonal architecture powered by ZIO's dependency injection container (`ZLayer`):

```
┌────────────────────────────────────────────────────────┐
│                   Angular Client                       │
└───────────────────────────┬────────────────────────────┘
                            │ HTTP / JSON (CORS Enabled)
                            ▼
┌────────────────────────────────────────────────────────┐
│                sttp Tapir + ZIO HTTP                   │
│  - Endpoint definitions (Inputs, Outputs, Errors)      │
│  - Auto-generated OpenAPI 3.0 (/docs/openapi.json)     │
│  - Integrated Swagger UI (/swagger or /docs)           │
└───────────────────────────┬────────────────────────────┘
                            │ ZLayer[ContentService & QuizService & ...]
                            ▼
┌────────────────────────────────────────────────────────┐
│                    Domain Services                     │
│  - ContentService: Article indexing & retrieval        │
│  - QuizService: Answer scoring & attempt logging       │
│  - AnnouncementService: Priority publishing            │
│  - MetadataService: Home & Developer credits           │
└───────────────────────────┬────────────────────────────┘
                            │ ZLayer[Repositories]
                            ▼
┌────────────────────────────────────────────────────────┐
│                  Repositories (ZIO Quill)              │
│  - ArticleRepository                                   │
│  - QuizRepository                                      │
│  - AnnouncementRepository                              │
└───────────────────────────┬────────────────────────────┘
                            │ HikariCP DataSource
                            ▼
┌────────────────────────────────────────────────────────┐
│               PostgreSQL Database (Flyway)             │
└────────────────────────────────────────────────────────┘
```

---

## Technology Stack

| Component | Library / Tool | Version | Rationale |
| :--- | :--- | :--- | :--- |
| **Language** | Scala | 3.3.3 (LTS) | Modern syntax, union types, native enums, compiler speed. |
| **Runtime** | ZIO | 2.1.x | Structured concurrency, resource safety, typed error handling `ZIO[R, E, A]`. |
| **HTTP Server** | zio-http | 3.0.x | Native ZIO asynchronous HTTP server with non-blocking I/O. |
| **API & Swagger** | sttp Tapir | 1.10.x | Code-first endpoint definitions automatically generating OpenAPI 3.0 and Swagger UI. |
| **JSON** | zio-json | 0.7.x | Fast, zero-reflection compile-time JSON codecs. |
| **Database ORM** | zio-quill-jdbc-postgres | 4.8.x | Compile-time SQL verification, idiomatic Scala query syntax. |
| **Migrations** | Flyway | 10.x | Automated versioned schema management at application startup. |
| **Connection Pool** | HikariCP | 5.1.x | Industry benchmark for high-performance JDBC connection pooling. |
| **Build Tool** | sbt | 1.10.x | Standard Scala build tool with multi-project support. |

---

## API Endpoints & Swagger / Tapir Schema

Tapir allows endpoints to be declared as declarative values:

```scala
// Example Tapir Endpoint Definition
val getQuizQuestionsEndpoint: PublicEndpoint[Unit, ApiError, List[QuizQuestionDto], Any] =
  endpoint.get
    .in("api" / "v1" / "quiz" / "questions")
    .out(jsonBody[List[QuizQuestionDto]])
    .errorOut(jsonBody[ApiError])
    .summary("Get active quiz questions")
    .description("Returns questions and multiple-choice options without revealing correct answers.")
```

From these definitions, `SwaggerInterpreter` generates the Swagger UI bundle mounted under `/docs`:

### Endpoint Catalog:
- **Swagger / OpenAPI**:
  - `GET /docs` &rarr; Interactive Swagger UI
  - `GET /docs/openapi.json` &rarr; OpenAPI 3.0 specification
- **Home & Metadata**:
  - `GET /api/v1/home/summary` &rarr; Contest summary, metrics, urgent alerts
  - `GET /api/v1/developers` &rarr; Developer team profiles, tech stack justification
- **Educational Content**:
  - `GET /api/v1/content/categories` &rarr; Categories list
  - `GET /api/v1/content/articles` &rarr; Paginated articles list
  - `GET /api/v1/content/articles/{slug}` &rarr; Full article markdown/HTML
- **Interactive Quiz**:
  - `GET /api/v1/quiz/questions` &rarr; Quiz questions and option choices
  - `POST /api/v1/quiz/submit` &rarr; Evaluate submitted answers, store attempt
  - `GET /api/v1/quiz/attempts/{id}` &rarr; Attempt score and qualitative feedback
- **Communication Hub**:
  - `GET /api/v1/announcements` &rarr; Academic announcements (filtered by priority)
  - `POST /api/v1/announcements` &rarr; Create new teacher announcement
  - `POST /api/v1/queries` &rarr; Submit student question

---

## Relational Database Schema (PostgreSQL)

Managed via Flyway migrations (`db/migration/V1__init_schema.sql` and `V2__seed_initial_data.sql`):

```sql
-- Categories for Educational Content
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- Educational Articles
CREATE TABLE articles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    excerpt TEXT NOT NULL,
    content TEXT NOT NULL,
    author VARCHAR(150) NOT NULL DEFAULT 'EduConnect Team',
    read_time_minutes INT NOT NULL DEFAULT 5,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Quiz Questions
CREATE TABLE quiz_questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prompt TEXT NOT NULL,
    explanation TEXT,
    order_index INT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Quiz Options
CREATE TABLE quiz_options (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    question_id UUID NOT NULL REFERENCES quiz_questions(id) ON DELETE CASCADE,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    points INT NOT NULL DEFAULT 10
);

-- Quiz Attempts
CREATE TABLE quiz_attempts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    participant_name VARCHAR(150) NOT NULL,
    score INT NOT NULL,
    total_score INT NOT NULL,
    percentage NUMERIC(5,2) NOT NULL,
    feedback TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Announcements (Communication Hub)
CREATE TABLE announcements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author_name VARCHAR(150) NOT NULL,
    author_role VARCHAR(50) NOT NULL DEFAULT 'Docente',
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL', -- 'URGENT', 'NORMAL', 'INFO'
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
```

---

## Security, CORS & Angular Integration

1. **CORS Middleware**: Configured in ZIO HTTP with allowed origins (`http://localhost:4200` for development and production host), methods (`GET, POST, PUT, DELETE, OPTIONS`), and headers (`Content-Type, Authorization`).
2. **Error Handling**: A standardized `ApiError` case class mapped to appropriate HTTP status codes:
   - `NotFoundError` &rarr; 404
   - `ValidationError` &rarr; 400
   - `InternalError` &rarr; 500
3. **OpenAPI Generation for Angular**:
   The frontend team can consume `/docs/openapi.json` with `@openapitools/openapi-generator-cli` to automatically generate TypeScript services and models:
   ```bash
   npx @openapitools/openapi-generator-cli generate -i http://localhost:8080/docs/openapi.json -g typescript-angular -o src/app/api
   ```

---

## Repository Project Structure

```
EduConnect_backend_26/
├── build.sbt
├── project/
│   ├── build.properties
│   └── plugins.sbt
├── src/
│   ├── main/
│   │   ├── resources/
│   │   │   ├── application.conf
│   │   │   └── db/migration/
│   │   │       ├── V1__init_schema.sql
│   │   │       └── V2__seed_initial_data.sql
│   │   └── scala/educonnect/
│   │       ├── Main.scala
│   │       ├── config/
│   │       │   └── AppConfig.scala
│   │       ├── domain/
│   │       │   ├── models.scala
│   │       │   └── errors.scala
│   │       ├── repository/
│   │       │   ├── ArticleRepository.scala
│   │       │   ├── QuizRepository.scala
│   │       │   └── AnnouncementRepository.scala
│   │       ├── service/
│   │       │   ├── ContentService.scala
│   │       │   ├── QuizService.scala
│   │       │   └── AnnouncementService.scala
│   │       └── api/
│   │           ├── Endpoints.scala
│   │           ├── Routes.scala
│   │           └── SwaggerDocs.scala
│   └── test/
│       └── scala/educonnect/
│           └── QuizServiceSpec.scala
├── docker-compose.yml
└── README.md
```
