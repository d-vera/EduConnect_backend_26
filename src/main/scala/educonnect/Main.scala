package educonnect

import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import org.slf4j.LoggerFactory
import zio.*
import zio.http.*
import educonnect.api.Routes
import educonnect.config.AppConfig
import educonnect.db.MigrationRunner
import educonnect.repository.*
import educonnect.service.*

object Main extends ZIOAppDefault {

  private val log = LoggerFactory.getLogger(getClass)

  val run: ZIO[Any, Throwable, Unit] = {
    for {
      config <- ZIO.service[AppConfig]
      _ <- ZIO.logInfo(s"Starting EduConnect Backend on ${config.server.host}:${config.server.port}...")

      // 1. Run database migrations via Flyway
      _ <- MigrationRunner.run(config.database)
        .catchAll { err =>
          ZIO.logError(s"Database migration failed: ${err.getMessage}. Ensure PostgreSQL is accessible.") *>
            ZIO.fail(err)
        }

      // 2. Fetch business services from ZIO environment
      contentService <- ZIO.service[ContentService]
      quizService <- ZIO.service[QuizService]
      announcementService <- ZIO.service[AnnouncementService]
      metadataService <- ZIO.service[MetadataService]

      // 3. Mount routes with Tapir endpoints, Swagger docs, and CORS middleware
      httpRoutes = Routes.make(
        contentService,
        quizService,
        announcementService,
        metadataService,
        config.server.allowedOrigins
      )

      _ <- ZIO.logInfo("=================================================================")
      _ <- ZIO.logInfo("🎓 EduConnect Backend (Scala 3 + ZIO 2 + sttp Tapir) is RUNNING!")
      _ <- ZIO.logInfo(s"📍 REST API:     http://${config.server.host}:${config.server.port}/api/v1")
      _ <- ZIO.logInfo(s"📚 Swagger UI:   http://${config.server.host}:${config.server.port}/docs")
      _ <- ZIO.logInfo(s"📄 OpenAPI Spec: http://${config.server.host}:${config.server.port}/docs/openapi.json")
      _ <- ZIO.logInfo("=================================================================")

      // 4. Launch HTTP Server
      _ <- Server.serve(httpRoutes)
    } yield ()
  }.provide(
    AppConfig.live,
    Quill.DataSource.fromPrefix("database"),
    Quill.Postgres.fromNamingStrategy(SnakeCase),
    ArticleRepositoryLive.live,
    QuizRepositoryLive.live,
    AnnouncementRepositoryLive.live,
    ContentServiceLive.live,
    QuizServiceLive.live,
    AnnouncementServiceLive.live,
    MetadataServiceLive.live,
    Server.defaultWithPort(8080)
  )
}
