package educonnect.repository

import java.util.UUID
import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.*
import educonnect.domain.{Announcement, StudentQuery}

trait AnnouncementRepository {
  def listAnnouncements(priority: Option[String], limit: Int): Task[List[Announcement]]
  def insertAnnouncement(announcement: Announcement): Task[Announcement]
  def countAnnouncements: Task[Long]
  def insertStudentQuery(query: StudentQuery): Task[StudentQuery]
  def listStudentQueries: Task[List[StudentQuery]]
}

final case class AnnouncementRepositoryLive(quill: Quill.Postgres[SnakeCase]) extends AnnouncementRepository {
  import quill.*

  inline def announcementsSchema = quote {
    querySchema[Announcement]("announcements")
  }

  inline def queriesSchema = quote {
    querySchema[StudentQuery]("student_queries")
  }

  override def listAnnouncements(priority: Option[String], limit: Int): Task[List[Announcement]] = {
    priority match {
      case Some(p) =>
        run(
          announcementsSchema
            .filter(_.priority == lift(p))
            .take(lift(limit))
        )
      case None =>
        run(
          announcementsSchema
            .take(lift(limit))
        )
    }
  }

  override def insertAnnouncement(announcement: Announcement): Task[Announcement] =
    run(announcementsSchema.insertValue(lift(announcement))).as(announcement)

  override def countAnnouncements: Task[Long] =
    run(announcementsSchema.size)

  override def insertStudentQuery(query: StudentQuery): Task[StudentQuery] =
    run(queriesSchema.insertValue(lift(query))).as(query)

  override def listStudentQueries: Task[List[StudentQuery]] =
    run(queriesSchema.take(50))
}

object AnnouncementRepositoryLive {
  val live: ZLayer[Quill.Postgres[SnakeCase], Nothing, AnnouncementRepository] =
    ZLayer.fromFunction(AnnouncementRepositoryLive.apply _)
}
