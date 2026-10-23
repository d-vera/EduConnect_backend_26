package educonnect.api

import io.circe.syntax.*
import sttp.apispec.openapi.Info
import sttp.apispec.openapi.circe.*
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.*
import zio.*
import zio.http.*
import educonnect.domain.*
import educonnect.service.*

object Routes {

  val apiInfo: Info = Info(
    title = "EduConnect REST API",
    version = "1.0.0",
    description = Some(
      "### Especificaciones OpenAPI 3.1 descargables:\n" +
      "- 📄 **OpenAPI JSON**: [http://localhost:8080/api/v1/openapi.json](http://localhost:8080/api/v1/openapi.json)\n" +
      "- 📄 **OpenAPI YAML**: [/docs/docs.yaml](http://localhost:8080/docs/docs.yaml)\n\n" +
      "Plataforma de Comunicación Efectiva e Interacción Académica para **Diseño y Programación Web III** (Gestión 2026).\n" +
      "Desarrollado con **Scala 3, ZIO 2 y sttp Tapir**."
    )
  )

  val openApiSpec = OpenAPIDocsInterpreter().toOpenAPI(Endpoints.allEndpoints, apiInfo)
  val openApiJsonString: String = openApiSpec.asJson.spaces2

  val openApiJsonEndpoint: ZServerEndpoint[Any, Any] = sttp.tapir.ztapir.endpoint.get
    .in("docs" / "openapi.json")
    .out(stringBody)
    .out(header("Content-Type", "application/json"))
    .summary("OpenAPI 3.1 specification in JSON format")
    .zServerLogic(_ => ZIO.succeed(openApiJsonString))

  val openApiJsonApiEndpoint: ZServerEndpoint[Any, Any] = sttp.tapir.ztapir.endpoint.get
    .in("api" / "v1" / "openapi.json")
    .out(stringBody)
    .out(header("Content-Type", "application/json"))
    .summary("OpenAPI 3.1 specification in JSON format (API alias)")
    .zServerLogic(_ => ZIO.succeed(openApiJsonString))

  def make(
      contentService: ContentService,
      quizService: QuizService,
      announcementService: AnnouncementService,
      metadataService: MetadataService,
      allowedOrigins: List[String]
  ): Routes[Any, Response] = {

    val serverEndpoints: List[ZServerEndpoint[Any, Any]] = List(
      Endpoints.getHomeSummaryEndpoint.zServerLogic(_ => metadataService.getHomeSummary),
      Endpoints.getDeveloperInfoEndpoint.zServerLogic(_ => metadataService.getDeveloperInfo),
      Endpoints.getCategoriesEndpoint.zServerLogic(_ => contentService.getCategories),
      Endpoints.getArticlesEndpoint.zServerLogic(c => contentService.getArticles(c)),
      Endpoints.getArticleBySlugEndpoint.zServerLogic(slug => contentService.getArticleBySlug(slug)),
      Endpoints.getQuizQuestionsEndpoint.zServerLogic(_ => quizService.getQuestions),
      Endpoints.submitQuizEndpoint.zServerLogic(req => quizService.submitQuiz(req)),
      Endpoints.getQuizAttemptEndpoint.zServerLogic(id => quizService.getAttempt(id)),
      Endpoints.getAnnouncementsEndpoint.zServerLogic(p => announcementService.listAnnouncements(p)),
      Endpoints.createAnnouncementEndpoint.zServerLogic(req => announcementService.createAnnouncement(req)),
      Endpoints.submitStudentQueryEndpoint.zServerLogic(req => announcementService.submitStudentQuery(req)),
      Endpoints.listStudentQueriesEndpoint.zServerLogic(_ => announcementService.listStudentQueries),
      openApiJsonEndpoint,
      openApiJsonApiEndpoint
    )

    val swaggerEndpoints: List[ZServerEndpoint[Any, Any]] = SwaggerInterpreter().fromEndpoints[Task](
      Endpoints.allEndpoints,
      apiInfo
    )

    val routes: Routes[Any, Response] = ZioHttpInterpreter().toHttp(serverEndpoints ++ swaggerEndpoints)

    val corsConfig = Middleware.CorsConfig(
      allowedOrigin = { origin =>
        Some(Header.AccessControlAllowOrigin.All)
      }
    )

    routes @@ Middleware.cors(corsConfig)
  }
}
