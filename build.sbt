scalaVersion := "3.3.3"

name := "educonnect-backend"
organization := "com.educonnect"
version := "1.0.0"

Compile / mainClass := Some("educonnect.Main")

val zioVersion = "2.1.9"
val tapirVersion = "1.10.15"
val quillVersion = "4.8.5"
val flywayVersion = "10.17.0"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-streams" % zioVersion,
  "dev.zio" %% "zio-json" % "0.7.3",
  "dev.zio" %% "zio-config" % "4.0.2",
  "dev.zio" %% "zio-config-typesafe" % "4.0.2",
  "dev.zio" %% "zio-config-magnolia" % "4.0.2",

  // Tapir & HTTP
  "com.softwaremill.sttp.tapir" %% "tapir-zio" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-zio" % tapirVersion,

  // Database & Migrations
  "io.getquill" %% "quill-jdbc-zio" % quillVersion,
  "org.postgresql" % "postgresql" % "42.7.4",
  "com.zaxxer" % "HikariCP" % "5.1.0",
  "org.flywaydb" % "flyway-core" % flywayVersion,
  "org.flywaydb" % "flyway-database-postgresql" % flywayVersion,

  // Logging
  "ch.qos.logback" % "logback-classic" % "1.5.8",

  // Testing
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
  "dev.zio" %% "zio-test-magnolia" % zioVersion % Test
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

lazy val dbMigrate = taskKey[Unit]("Run Flyway database migrations directly")
dbMigrate := (Compile / runMain).toTask(" educonnect.db.MigrationRunner").value
