package educonnect.repository

import java.util.UUID
import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.*
import educonnect.domain.{QuizAttempt, QuizOption, QuizQuestion}

trait QuizRepository {
  def listActiveQuestions: Task[List[QuizQuestion]]
  def listOptionsByQuestionId(questionId: UUID): Task[List[QuizOption]]
  def listAllActiveQuestionsWithOptions: Task[List[(QuizQuestion, List[QuizOption])]]
  def findOptionsByIds(optionIds: List[UUID]): Task[List[QuizOption]]
  def saveAttempt(attempt: QuizAttempt): Task[QuizAttempt]
  def findAttemptById(id: UUID): Task[Option[QuizAttempt]]
  def countQuestions: Task[Long]
}

final case class QuizRepositoryLive(quill: Quill.Postgres[SnakeCase]) extends QuizRepository {
  import quill.*

  inline def questionsSchema = quote {
    querySchema[QuizQuestion]("quiz_questions")
  }

  inline def optionsSchema = quote {
    querySchema[QuizOption]("quiz_options")
  }

  inline def attemptsSchema = quote {
    querySchema[QuizAttempt]("quiz_attempts")
  }

  override def listActiveQuestions: Task[List[QuizQuestion]] =
    run(questionsSchema.filter(_.isActive).sortBy(_.orderIndex)(Ord.asc))

  override def listOptionsByQuestionId(questionId: UUID): Task[List[QuizOption]] =
    run(optionsSchema.filter(_.questionId == lift(questionId)))

  override def listAllActiveQuestionsWithOptions: Task[List[(QuizQuestion, List[QuizOption])]] = {
    for {
      questions <- listActiveQuestions
      qIds = questions.map(_.id)
      options <- run(optionsSchema.filter(o => liftQuery(qIds).contains(o.questionId)))
      optionsByQuestion = options.groupBy(_.questionId)
    } yield questions.map(q => (q, optionsByQuestion.getOrElse(q.id, Nil)))
  }

  override def findOptionsByIds(optionIds: List[UUID]): Task[List[QuizOption]] =
    if (optionIds.isEmpty) ZIO.succeed(Nil)
    else run(optionsSchema.filter(o => liftQuery(optionIds).contains(o.id)))

  override def saveAttempt(attempt: QuizAttempt): Task[QuizAttempt] =
    run(attemptsSchema.insertValue(lift(attempt))).as(attempt)

  override def findAttemptById(id: UUID): Task[Option[QuizAttempt]] =
    run(attemptsSchema.filter(_.id == lift(id))).map(_.headOption)

  override def countQuestions: Task[Long] =
    run(questionsSchema.filter(_.isActive).size)
}

object QuizRepositoryLive {
  val live: ZLayer[Quill.Postgres[SnakeCase], Nothing, QuizRepository] =
    ZLayer.fromFunction(QuizRepositoryLive.apply _)
}
