package educonnect

import java.nio.file.{Files, Paths}
import educonnect.api.Routes

object ExportOpenApi {
  def main(args: Array[String]): Unit = {
    val json = Routes.openApiJsonString
    Files.write(Paths.get("openapi.json"), json.getBytes("UTF-8"))
    println("Successfully generated openapi.json at project root!")
  }
}
