package educonnect.service

import java.time.Instant
import java.util.UUID
import zio.*
import educonnect.domain.*
import educonnect.repository.AnnouncementRepository

trait AnnouncementService {
  def listAnnouncements(priority: Option[String]): IO[ApiError, List[Announcement]]
  def createAnnouncement(req: AnnouncementCreateRequest): IO[ApiError, Announcement]
  def submitStudentQuery(req: StudentQueryCreateRequest): IO[ApiError, StudentQuery]
  def listStudentQueries: IO[ApiError, List[StudentQuery]]
}

final case class AnnouncementServiceLive(repo: AnnouncementRepository) extends AnnouncementService {

  private val validPriorities = Set("URGENT", "NORMAL", "INFO")

  override def listAnnouncements(priority: Option[String]): IO[ApiError, List[Announcement]] = {
    priority match {
      case Some(p) if !validPriorities.contains(p.toUpperCase) =>
        ZIO.fail(ValidationError(s"Prioridad inválida '$p'. Valores permitidos: ${validPriorities.mkString(", ")}", Some("priority")))
      case opt =>
        repo.listAnnouncements(opt.map(_.toUpperCase), limit = 20)
          .mapError(e => DatabaseError(s"Error retrieving announcements: ${e.getMessage}"))
    }
  }

  override def createAnnouncement(req: AnnouncementCreateRequest): IO[ApiError, Announcement] = {
    val normPriority = req.priority.trim.toUpperCase
    if (req.title.trim.isEmpty) {
      ZIO.fail(ValidationError("El título del aviso no puede estar vacío", Some("title")))
    } else if (req.content.trim.isEmpty) {
      ZIO.fail(ValidationError("El contenido del aviso no puede estar vacío", Some("content")))
    } else if (req.authorName.trim.isEmpty) {
      ZIO.fail(ValidationError("El nombre del autor no puede estar vacío", Some("authorName")))
    } else if (!validPriorities.contains(normPriority)) {
      ZIO.fail(ValidationError(s"Prioridad '$normPriority' inválida. Valores permitidos: ${validPriorities.mkString(", ")}", Some("priority")))
    } else {
      val announcement = Announcement(
        id = UUID.randomUUID(),
        title = req.title.trim,
        content = req.content.trim,
        authorName = req.authorName.trim,
        authorRole = if (req.authorRole.trim.isEmpty) "Docente" else req.authorRole.trim,
        priority = normPriority,
        createdAt = Some(Instant.now())
      )
      repo.insertAnnouncement(announcement)
        .mapError(e => DatabaseError(s"Error creating announcement: ${e.getMessage}"))
    }
  }

  override def submitStudentQuery(req: StudentQueryCreateRequest): IO[ApiError, StudentQuery] = {
    if (req.studentName.trim.isEmpty) {
      ZIO.fail(ValidationError("El nombre del estudiante es obligatorio", Some("studentName")))
    } else if (req.studentEmail.trim.isEmpty || !req.studentEmail.contains("@")) {
      ZIO.fail(ValidationError("Debe ingresar un correo electrónico institucional o personal válido", Some("studentEmail")))
    } else if (req.topic.trim.isEmpty) {
      ZIO.fail(ValidationError("El tema de la consulta es obligatorio", Some("topic")))
    } else if (req.message.trim.isEmpty) {
      ZIO.fail(ValidationError("El mensaje de consulta no puede estar vacío", Some("message")))
    } else {
      val query = StudentQuery(
        id = UUID.randomUUID(),
        studentName = req.studentName.trim,
        studentEmail = req.studentEmail.trim,
        topic = req.topic.trim,
        message = req.message.trim,
        status = "PENDING",
        createdAt = Some(Instant.now())
      )
      repo.insertStudentQuery(query)
        .mapError(e => DatabaseError(s"Error registering student query: ${e.getMessage}"))
    }
  }

  override def listStudentQueries: IO[ApiError, List[StudentQuery]] =
    repo.listStudentQueries
      .mapError(e => DatabaseError(s"Error retrieving student queries: ${e.getMessage}"))
}

object AnnouncementServiceLive {
  val live: ZLayer[AnnouncementRepository, Nothing, AnnouncementService] =
    ZLayer.fromFunction(AnnouncementServiceLive.apply _)
}
