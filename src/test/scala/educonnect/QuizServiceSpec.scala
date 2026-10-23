package educonnect

import java.util.UUID
import zio.*
import zio.test.*
import zio.test.Assertion.*
import educonnect.domain.*
import educonnect.repository.QuizRepository
import educonnect.service.{QuizService, QuizServiceLive}

object QuizServiceSpec extends ZIOSpecDefault {

  class FakeQuizRepository extends QuizRepository {
    val q1Id = UUID.randomUUID()
    val q2Id = UUID.randomUUID()
    val q3Id = UUID.randomUUID()
    val q4Id = UUID.randomUUID()

    val opt1Correct = QuizOption(UUID.randomUUID(), q1Id, "Respuesta Asertiva", isCorrect = true, points = 25)
    val opt1Wrong = QuizOption(UUID.randomUUID(), q1Id, "Respuesta Incorrecta", isCorrect = false, points = 0)

    val questions = List(
      QuizQuestion(q1Id, "Pregunta 1", None, 1, true),
      QuizQuestion(q2Id, "Pregunta 2", None, 2, true),
      QuizQuestion(q3Id, "Pregunta 3", None, 3, true),
      QuizQuestion(q4Id, "Pregunta 4", None, 4, true)
    )

    override def listActiveQuestions: Task[List[QuizQuestion]] = ZIO.succeed(questions)

    override def listOptionsByQuestionId(questionId: UUID): Task[List[QuizOption]] =
      ZIO.succeed(List(opt1Correct, opt1Wrong))

    override def listAllActiveQuestionsWithOptions: Task[List[(QuizQuestion, List[QuizOption])]] =
      ZIO.succeed(questions.map(q => (q, List(opt1Correct, opt1Wrong))))

    override def findOptionsByIds(optionIds: List[UUID]): Task[List[QuizOption]] =
      ZIO.succeed(List(opt1Correct).filter(o => optionIds.contains(o.id)))

    override def saveAttempt(attempt: QuizAttempt): Task[QuizAttempt] = ZIO.succeed(attempt)

    override def findAttemptById(id: UUID): Task[Option[QuizAttempt]] = ZIO.none

    override def countQuestions: Task[Long] = ZIO.succeed(4L)
  }

  def spec = suite("QuizServiceSpec")(
    test("Correctly scores quiz submission and provides qualitative feedback") {
      val fakeRepo = new FakeQuizRepository
      val service = QuizServiceLive(fakeRepo)

      val request = QuizSubmissionRequest(
        participantName = "Dennis Vera",
        selectedOptionIds = List(fakeRepo.opt1Correct.id)
      )

      for {
        attempt <- service.submitQuiz(request)
      } yield assertTrue(
        attempt.participantName == "Dennis Vera",
        attempt.score == 25,
        attempt.totalScore == 100,
        attempt.percentage == BigDecimal("25.00"),
        attempt.feedback.contains("Oportunidad de Crecimiento")
      )
    },
    test("Rejects empty participant name") {
      val fakeRepo = new FakeQuizRepository
      val service = QuizServiceLive(fakeRepo)

      val request = QuizSubmissionRequest(
        participantName = "   ",
        selectedOptionIds = List(fakeRepo.opt1Correct.id)
      )

      for {
        exit <- service.submitQuiz(request).exit
      } yield assertTrue(exit.isFailure)
    }
  )
}
