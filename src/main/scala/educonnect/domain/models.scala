package educonnect.domain

import java.time.Instant
import java.util.UUID
import zio.json.*
import sttp.tapir.Schema

// --- Categories ---
final case class Category(
    id: UUID,
    name: String,
    slug: String,
    description: Option[String] = None,
    createdAt: Option[Instant] = None
)

object Category {
  given JsonCodec[Category] = DeriveJsonCodec.gen[Category]
  given Schema[Category] = Schema.derived[Category]
}

// --- Articles ---
final case class Article(
    id: UUID,
    categoryId: UUID,
    title: String,
    slug: String,
    excerpt: String,
    content: String,
    author: String,
    readTimeMinutes: Int,
    createdAt: Option[Instant] = None
)

object Article {
  given JsonCodec[Article] = DeriveJsonCodec.gen[Article]
  given Schema[Article] = Schema.derived[Article]
}

final case class ArticleSummary(
    id: UUID,
    categoryId: UUID,
    categoryName: String,
    title: String,
    slug: String,
    excerpt: String,
    author: String,
    readTimeMinutes: Int,
    createdAt: Option[Instant] = None
)

object ArticleSummary {
  given JsonCodec[ArticleSummary] = DeriveJsonCodec.gen[ArticleSummary]
  given Schema[ArticleSummary] = Schema.derived[ArticleSummary]
}

// --- Quiz ---
final case class QuizQuestion(
    id: UUID,
    prompt: String,
    explanation: Option[String] = None,
    orderIndex: Int,
    isActive: Boolean = true,
    createdAt: Option[Instant] = None
)

object QuizQuestion {
  given JsonCodec[QuizQuestion] = DeriveJsonCodec.gen[QuizQuestion]
  given Schema[QuizQuestion] = Schema.derived[QuizQuestion]
}

final case class QuizOption(
    id: UUID,
    questionId: UUID,
    optionText: String,
    isCorrect: Boolean,
    points: Int
)

object QuizOption {
  given JsonCodec[QuizOption] = DeriveJsonCodec.gen[QuizOption]
  given Schema[QuizOption] = Schema.derived[QuizOption]
}

final case class QuizOptionDto(
    id: UUID,
    optionText: String
)

object QuizOptionDto {
  given JsonCodec[QuizOptionDto] = DeriveJsonCodec.gen[QuizOptionDto]
  given Schema[QuizOptionDto] = Schema.derived[QuizOptionDto]
}

final case class QuizQuestionDto(
    id: UUID,
    prompt: String,
    orderIndex: Int,
    options: List[QuizOptionDto]
)

object QuizQuestionDto {
  given JsonCodec[QuizQuestionDto] = DeriveJsonCodec.gen[QuizQuestionDto]
  given Schema[QuizQuestionDto] = Schema.derived[QuizQuestionDto]
}

final case class QuizSubmissionRequest(
    participantName: String,
    selectedOptionIds: List[UUID]
)

object QuizSubmissionRequest {
  given JsonCodec[QuizSubmissionRequest] = DeriveJsonCodec.gen[QuizSubmissionRequest]
  given Schema[QuizSubmissionRequest] = Schema.derived[QuizSubmissionRequest]
}

final case class QuizAttempt(
    id: UUID,
    participantName: String,
    score: Int,
    totalScore: Int,
    percentage: BigDecimal,
    feedback: String,
    createdAt: Option[Instant] = None
)

object QuizAttempt {
  given JsonCodec[QuizAttempt] = DeriveJsonCodec.gen[QuizAttempt]
  given Schema[QuizAttempt] = Schema.derived[QuizAttempt]
}

// --- Announcements ---
final case class Announcement(
    id: UUID,
    title: String,
    content: String,
    authorName: String,
    authorRole: String,
    priority: String,
    createdAt: Option[Instant] = None
)

object Announcement {
  given JsonCodec[Announcement] = DeriveJsonCodec.gen[Announcement]
  given Schema[Announcement] = Schema.derived[Announcement]
}

final case class AnnouncementCreateRequest(
    title: String,
    content: String,
    authorName: String,
    authorRole: String = "Docente",
    priority: String = "NORMAL"
)

object AnnouncementCreateRequest {
  given JsonCodec[AnnouncementCreateRequest] = DeriveJsonCodec.gen[AnnouncementCreateRequest]
  given Schema[AnnouncementCreateRequest] = Schema.derived[AnnouncementCreateRequest]
}

// --- Student Queries ---
final case class StudentQuery(
    id: UUID,
    studentName: String,
    studentEmail: String,
    topic: String,
    message: String,
    status: String = "PENDING",
    createdAt: Option[Instant] = None
)

object StudentQuery {
  given JsonCodec[StudentQuery] = DeriveJsonCodec.gen[StudentQuery]
  given Schema[StudentQuery] = Schema.derived[StudentQuery]
}

final case class StudentQueryCreateRequest(
    studentName: String,
    studentEmail: String,
    topic: String,
    message: String
)

object StudentQueryCreateRequest {
  given JsonCodec[StudentQueryCreateRequest] = DeriveJsonCodec.gen[StudentQueryCreateRequest]
  given Schema[StudentQueryCreateRequest] = Schema.derived[StudentQueryCreateRequest]
}

// --- Developer & Tech Stack Information ---
final case class TeamMember(
    name: String,
    role: String,
    github: Option[String] = None,
    bio: String
)

object TeamMember {
  given JsonCodec[TeamMember] = DeriveJsonCodec.gen[TeamMember]
  given Schema[TeamMember] = Schema.derived[TeamMember]
}

final case class TechStackItem(
    layer: String,
    name: String,
    version: String,
    justification: String
)

object TechStackItem {
  given JsonCodec[TechStackItem] = DeriveJsonCodec.gen[TechStackItem]
  given Schema[TechStackItem] = Schema.derived[TechStackItem]
}

final case class DeveloperInfo(
    teamName: String,
    course: String,
    institution: String,
    academicYear: Int,
    members: List[TeamMember],
    techStack: List[TechStackItem],
    projectObjective: String,
    features: List[String]
)

object DeveloperInfo {
  given JsonCodec[DeveloperInfo] = DeriveJsonCodec.gen[DeveloperInfo]
  given Schema[DeveloperInfo] = Schema.derived[DeveloperInfo]
}

// --- Home Summary ---
final case class HomeSummary(
    title: String,
    motto: String,
    theme: String,
    articlesCount: Long,
    quizQuestionsCount: Long,
    announcementsCount: Long,
    recentAnnouncements: List[Announcement],
    featuredArticles: List[ArticleSummary]
)

object HomeSummary {
  given JsonCodec[HomeSummary] = DeriveJsonCodec.gen[HomeSummary]
  given Schema[HomeSummary] = Schema.derived[HomeSummary]
}
