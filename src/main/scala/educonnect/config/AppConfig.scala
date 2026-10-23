package educonnect.config

import zio.*
import com.typesafe.config.ConfigFactory

final case class ServerConfig(
    host: String,
    port: Int,
    allowedOrigins: List[String]
)

final case class DatabaseConfig(
    url: String,
    host: String,
    port: Int,
    name: String,
    user: String,
    password: String,
    maximumPoolSize: Int
)

final case class AppConfig(
    server: ServerConfig,
    database: DatabaseConfig
)

object AppConfig:
  val live: ZLayer[Any, Throwable, AppConfig] = ZLayer.fromZIO {
    ZIO.attempt {
      val conf = ConfigFactory.load()
      val serverConf = conf.getConfig("app.server")
      val dbConf = conf.getConfig("app.database")

      import scala.jdk.CollectionConverters.*
      val origins = serverConf.getStringList("cors.allowedOrigins").asScala.toList

      AppConfig(
        server = ServerConfig(
          host = serverConf.getString("host"),
          port = serverConf.getInt("port"),
          allowedOrigins = origins
        ),
        database = DatabaseConfig(
          url = dbConf.getString("url"),
          host = dbConf.getString("host"),
          port = dbConf.getInt("port"),
          name = dbConf.getString("name"),
          user = dbConf.getString("user"),
          password = dbConf.getString("password"),
          maximumPoolSize = dbConf.getInt("maximumPoolSize")
        )
      )
    }
  }
