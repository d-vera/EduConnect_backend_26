package educonnect.api

import java.util.UUID
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.json.zio.*
import educonnect.domain.*

object Endpoints {

  val baseEndpoint: Endpoint[Unit, Unit, ApiError, Unit, Any] = endpoint
    .errorOut(
      oneOf[ApiError](
        oneOfVariant(StatusCode.NotFound, jsonBody[NotFoundError]),
        oneOfVariant(StatusCode.BadRequest, jsonBody[educonnect.domain.ValidationError]),
        oneOfVariant(StatusCode.InternalServerError, jsonBody[DatabaseError]),
        oneOfVariant(StatusCode.InternalServerError, jsonBody[InternalError])
      )
    )

  // --- Home & Metadata ---
  val getHomeSummaryEndpoint: Endpoint[Unit, Unit, ApiError, HomeSummary, Any] =
    baseEndpoint.get
      .in("api" / "v1" / "home" / "summary")
      .out(jsonBody[HomeSummary])
      .summary("Resumen para la página de inicio")
      .description("Retorna métricas globales, avisos urgentes y artículos destacados para la vista principal.")
      .tag("Home & Metadata")

  val getDeveloperInfoEndpoint: Endpoint[Unit, Unit, ApiError, DeveloperInfo, Any] =
    baseEndpoint.get
      .in("api" / "v1" / "developers")
      .out(jsonBody[DeveloperInfo])
      .summary("Información del equipo de desarrollo y stack tecnológico")
      .description("Detalla los créditos de los estudiantes desarrolladores, carrera, institución, justificación de arquitectura y características implementadas.")
      .tag("Home & Metadata")

  // --- Educational Content ---
  val getCategoriesEndpoint: Endpoint[Unit, Unit, ApiError, List[Category], Any] =
    baseEndpoint.get
      .in("api" / "v1" / "content" / "categories")
      .out(jsonBody[List[Category]])
      .summary("Listado de categorías pedagógicas")
      .description("Obtiene todas las áreas temáticas de comunicación (Asertividad, Escucha Activa, etc.).")
      .tag("Educational Content")

  val getArticlesEndpoint: Endpoint[Unit, Option[String], ApiError, List[ArticleSummary], Any] =
    baseEndpoint.get
      .in("api" / "v1" / "content" / "articles")
      .in(query[Option[String]]("category").description("Slug opcional de la categoría para filtrar artículos"))
      .out(jsonBody[List[ArticleSummary]])
      .summary("Listado de artículos formativos")
      .description("Retorna tarjetas/resúmenes de artículos educativos con opción de filtrado por categoría.")
      .tag("Educational Content")

  val getArticleBySlugEndpoint: Endpoint[Unit, String, ApiError, Article, Any] =
    baseEndpoint.get
      .in("api" / "v1" / "content" / "articles" / path[String]("slug").description("Slug identificador único del artículo"))
      .out(jsonBody[Article])
      .summary("Contenido detallado de un artículo")
      .description("Devuelve el artículo completo en formato Markdown estructurado con autor y tiempo de lectura.")
      .tag("Educational Content")

  // --- Interactive Quiz ---
  val getQuizQuestionsEndpoint: Endpoint[Unit, Unit, ApiError, List[QuizQuestionDto], Any] =
    baseEndpoint.get
      .in("api" / "v1" / "quiz" / "questions")
      .out(jsonBody[List[QuizQuestionDto]])
      .summary("Preguntas activas del cuestionario interactivo")
      .description("Retorna las preguntas ordenadas y sus alternativas de respuesta sin revelar las respuestas correctas.")
      .tag("Interactive Quiz")

  val submitQuizEndpoint: Endpoint[Unit, QuizSubmissionRequest, ApiError, QuizAttempt, Any] =
    baseEndpoint.post
      .in("api" / "v1" / "quiz" / "submit")
      .in(jsonBody[QuizSubmissionRequest])
      .out(statusCode(StatusCode.Created).and(jsonBody[QuizAttempt]))
      .summary("Envío y calificación automática del cuestionario")
      .description("Evalúa las respuestas enviadas, calcula la puntuación y porcentaje, genera retroalimentación cualitativa y almacena el intento.")
      .tag("Interactive Quiz")

  val getQuizAttemptEndpoint: Endpoint[Unit, UUID, ApiError, QuizAttempt, Any] =
    baseEndpoint.get
      .in("api" / "v1" / "quiz" / "attempts" / path[UUID]("id").description("ID único del intento"))
      .out(jsonBody[QuizAttempt])
      .summary("Consultar resultado de un intento de cuestionario")
      .description("Devuelve la calificación obtenida y la retroalimentación personalizada asociada.")
      .tag("Interactive Quiz")

  // --- Communication Hub (Announcements & Queries) ---
  val getAnnouncementsEndpoint: Endpoint[Unit, Option[String], ApiError, List[Announcement], Any] =
    baseEndpoint.get
      .in("api" / "v1" / "announcements")
      .in(query[Option[String]]("priority").description("Filtro opcional por nivel de prioridad (URGENT, NORMAL, INFO)"))
      .out(jsonBody[List[Announcement]])
      .summary("Avisos y comunicados académicos")
      .description("Lista de comunicados ordenados por prioridad y fecha de publicación.")
      .tag("Communication Hub")

  val createAnnouncementEndpoint: Endpoint[Unit, AnnouncementCreateRequest, ApiError, Announcement, Any] =
    baseEndpoint.post
      .in("api" / "v1" / "announcements")
      .in(jsonBody[AnnouncementCreateRequest])
      .out(statusCode(StatusCode.Created).and(jsonBody[Announcement]))
      .summary("Publicar nuevo comunicado docente")
      .description("Permite registrar un nuevo aviso en el canal de comunicación académica.")
      .tag("Communication Hub")

  val submitStudentQueryEndpoint: Endpoint[Unit, StudentQueryCreateRequest, ApiError, StudentQuery, Any] =
    baseEndpoint.post
      .in("api" / "v1" / "queries")
      .in(jsonBody[StudentQueryCreateRequest])
      .out(statusCode(StatusCode.Created).and(jsonBody[StudentQuery]))
      .summary("Enviar consulta o inquietud estudiantil")
      .description("Canal para que los estudiantes envíen dudas o retroalimentación sobre la materia o el proyecto.")
      .tag("Communication Hub")

  val listStudentQueriesEndpoint: Endpoint[Unit, Unit, ApiError, List[StudentQuery], Any] =
    baseEndpoint.get
      .in("api" / "v1" / "queries")
      .out(jsonBody[List[StudentQuery]])
      .summary("Listar consultas estudiantiles")
      .description("Permite a docentes revisar las inquietudes y consultas recibidas.")
      .tag("Communication Hub")

  val allEndpoints: List[AnyEndpoint] = List(
    getHomeSummaryEndpoint,
    getDeveloperInfoEndpoint,
    getCategoriesEndpoint,
    getArticlesEndpoint,
    getArticleBySlugEndpoint,
    getQuizQuestionsEndpoint,
    submitQuizEndpoint,
    getQuizAttemptEndpoint,
    getAnnouncementsEndpoint,
    createAnnouncementEndpoint,
    submitStudentQueryEndpoint,
    listStudentQueriesEndpoint
  )
}
