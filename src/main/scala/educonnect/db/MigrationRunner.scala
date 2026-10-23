package educonnect.db

import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import zio.*
import educonnect.config.{AppConfig, DatabaseConfig}

object MigrationRunner:
  private val log = LoggerFactory.getLogger(getClass)

  def run(config: DatabaseConfig): Task[Unit] =
    ZIO.attempt {
      log.info(s"Starting Flyway database migration against: ${config.url}")
      val flyway = Flyway.configure()
        .dataSource(config.url, config.user, config.password)
        .locations("classpath:db/migration")
        .baselineOnMigrate(true)
        .cleanDisabled(false)
        .load()

      val result = flyway.migrate()
      val currentVersion = Option(flyway.info().current()).map(_.getVersion.getVersion).getOrElse("empty")
      log.info(s"Flyway migrations completed successfully: ${result.migrationsExecuted} applied, schema version is now: $currentVersion")
    }.unit

  def main(args: Array[String]): Unit = {
    println("🚀 Running Flyway migrations via CLI...")
    val program = for {
      appConfig <- ZIO.service[AppConfig]
      _ <- run(appConfig.database)
    } yield ()

    Unsafe.unsafe { implicit unsafe =>
      Runtime.default.unsafe.run(program.provide(AppConfig.live)).getOrThrow()
    }
    println("✅ Flyway migrations finished!")
  }
