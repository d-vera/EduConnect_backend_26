package educonnect.service

import zio.*
import educonnect.domain.*
import educonnect.repository.{AnnouncementRepository, ArticleRepository, QuizRepository}

trait MetadataService {
  def getHomeSummary: IO[ApiError, HomeSummary]
  def getDeveloperInfo: UIO[DeveloperInfo]
}

final case class MetadataServiceLive(
    articleRepo: ArticleRepository,
    quizRepo: QuizRepository,
    announcementRepo: AnnouncementRepository
) extends MetadataService {

  override def getHomeSummary: IO[ApiError, HomeSummary] = {
    for {
      articlesCount <- articleRepo.countArticles
        .mapError(e => DatabaseError(e.getMessage))
      questionsCount <- quizRepo.countQuestions
        .mapError(e => DatabaseError(e.getMessage))
      announcementsCount <- announcementRepo.countAnnouncements
        .mapError(e => DatabaseError(e.getMessage))
      recentAnnouncements <- announcementRepo.listAnnouncements(None, limit = 3)
        .mapError(e => DatabaseError(e.getMessage))
      featuredArticles <- articleRepo.listArticles(None).map(_.take(3))
        .mapError(e => DatabaseError(e.getMessage))
    } yield HomeSummary(
      title = "EduConnect: Plataforma de Comunicación Efectiva e Interacción Académica",
      motto = "Construyendo puentes de entendimiento y diálogo asertivo en la comunidad educativa",
      theme = "Comunicación Efectiva (Temática Oficial #5 - Convocatoria Web III 2026)",
      articlesCount = articlesCount,
      quizQuestionsCount = questionsCount,
      announcementsCount = announcementsCount,
      recentAnnouncements = recentAnnouncements,
      featuredArticles = featuredArticles
    )
  }

  override def getDeveloperInfo: UIO[DeveloperInfo] = ZIO.succeed {
    DeveloperInfo(
      teamName = "EduConnect DevTeam",
      course = "Diseño y Programación Web III",
      institution = "Instituto Técnico Nacional de Comercio 'Federico Álvarez Plata' (Nocturno)",
      academicYear = 2026,
      members = List(
        TeamMember(
          name = "Dennis Vera",
          role = "Lead Backend & Systems Architect",
          github = Some("https://github.com/dennisvera"),
          bio = "Estudiante de Sistemas Informáticos apasionado por arquitecturas funcionales, concurrencia reactiva y desarrollo de software robusto."
        )
      ),
      techStack = List(
        TechStackItem(
          layer = "Backend Runtime",
          name = "Scala 3 & ZIO 2",
          version = "3.3.3 / 2.1.9",
          justification = "Provee concurrencia estructurada, seguridad de tipos en tiempo de compilación y manejo determinista de efectos secundarios sin excepciones no controladas."
        ),
        TechStackItem(
          layer = "API Specification & Documentation",
          name = "sttp Tapir & Swagger UI",
          version = "1.10.15",
          justification = "Definiciones de endpoints declarativas y code-first que garantizan sincronía total entre tipos de entrada/salida y la especificación OpenAPI 3.0."
        ),
        TechStackItem(
          layer = "HTTP Server Engine",
          name = "zio-http",
          version = "3.0.0-RC9",
          justification = "Servidor HTTP no bloqueante y asíncrono optimizado para alto rendimiento y bajo consumo de recursos en entornos de microservicios."
        ),
        TechStackItem(
          layer = "Database Access",
          name = "ZIO Quill (JDBC PostgreSQL)",
          version = "4.8.5",
          justification = "Consultas SQL generadas y verificadas en tiempo de compilación, eliminando fallos en runtime y desacoplando el esquema relacional."
        ),
        TechStackItem(
          layer = "Database Migrations",
          name = "Flyway Core",
          version = "10.17.0",
          justification = "Versionado automatizado del esquema relacional y carga inicial de contenido pedagógico al arrancar la aplicación."
        ),
        TechStackItem(
          layer = "Frontend Integration",
          name = "Angular & Tailwind CSS",
          version = "17+ / 3.4+",
          justification = "Consumo desacoplado mediante servicios HTTP tipados generables desde OpenAPI, con interfaz responsiva, accesible y de alta estética."
        )
      ),
      projectObjective = "Desarrollar una solución integral para fortalecer las habilidades comunicativas (asertividad, escucha activa, netiqueta y retroalimentación) entre estudiantes y docentes mediante herramientas pedagógicas e interactivas.",
      features = List(
        "Sección de Inicio con métricas globales y alertas institucionales urgentes.",
        "Módulo de Formación Educativa con artículos y categorías sobre comunicación efectiva.",
        "Cuestionario interactivo con evaluación automática, cálculo porcentual y retroalimentación cualitativa personalizada.",
        "Muro de Avisos Académicos categorizados por urgencia y buzón de consultas estudiantiles.",
        "Documentación interactiva Swagger UI disponible en /docs con especificación OpenAPI 3.0 para generación de clientes."
      )
    )
  }
}

object MetadataServiceLive {
  val live: ZLayer[ArticleRepository & QuizRepository & AnnouncementRepository, Nothing, MetadataService] =
    ZLayer.fromFunction(MetadataServiceLive.apply _)
}
