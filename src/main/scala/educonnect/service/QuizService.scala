package educonnect.service

import java.time.Instant
import java.util.UUID
import zio.*
import educonnect.domain.*
import educonnect.repository.QuizRepository

trait QuizService {
  def getQuestions: IO[ApiError, List[QuizQuestionDto]]
  def submitQuiz(request: QuizSubmissionRequest): IO[ApiError, QuizAttempt]
  def getAttempt(id: UUID): IO[ApiError, QuizAttempt]
}

final case class QuizServiceLive(repo: QuizRepository) extends QuizService {

  override def getQuestions: IO[ApiError, List[QuizQuestionDto]] =
    repo.listAllActiveQuestionsWithOptions
      .mapError(e => DatabaseError(s"Error retrieving quiz questions: ${e.getMessage}"))
      .map { questionsWithOptions =>
        questionsWithOptions.map { case (q, opts) =>
          QuizQuestionDto(
            id = q.id,
            prompt = q.prompt,
            orderIndex = q.orderIndex,
            options = opts.map(o => QuizOptionDto(id = o.id, optionText = o.optionText))
          )
        }
      }

  override def submitQuiz(request: QuizSubmissionRequest): IO[ApiError, QuizAttempt] = {
    if (request.participantName.trim.isEmpty) {
      ZIO.fail(ValidationError("El nombre del participante no puede estar vacío", Some("participantName")))
    } else if (request.selectedOptionIds.isEmpty) {
      ZIO.fail(ValidationError("Debe seleccionar al menos una respuesta para enviar el cuestionario", Some("selectedOptionIds")))
    } else {
      for {
        activeQuestions <- repo.listActiveQuestions
          .mapError(e => DatabaseError(e.getMessage))
        _ <- ZIO.fail(ValidationError("No hay preguntas activas en el cuestionario")).when(activeQuestions.isEmpty)
        totalScore = activeQuestions.size * 25 // 4 questions * 25 points = 100 max points

        selectedOptions <- repo.findOptionsByIds(request.selectedOptionIds)
          .mapError(e => DatabaseError(e.getMessage))

        earnedScore = selectedOptions.filter(_.isCorrect).map(_.points).sum
        percentageValue = if (totalScore > 0) BigDecimal(earnedScore * 100.0 / totalScore).setScale(2, BigDecimal.RoundingMode.HALF_UP) else BigDecimal(0)

        feedback = assignFeedback(percentageValue.toDouble)

        attempt = QuizAttempt(
          id = UUID.randomUUID(),
          participantName = request.participantName.trim,
          score = earnedScore,
          totalScore = totalScore,
          percentage = percentageValue,
          feedback = feedback,
          createdAt = Some(Instant.now())
        )

        saved <- repo.saveAttempt(attempt)
          .mapError(e => DatabaseError(s"Error saving quiz attempt: ${e.getMessage}"))
      } yield saved
    }
  }

  override def getAttempt(id: UUID): IO[ApiError, QuizAttempt] =
    repo.findAttemptById(id)
      .mapError(e => DatabaseError(s"Error retrieving quiz attempt: ${e.getMessage}"))
      .flatMap {
        case Some(attempt) => ZIO.succeed(attempt)
        case None => ZIO.fail(NotFoundError(s"Intento de quiz con ID '$id' no encontrado"))
      }

  private def assignFeedback(percentage: Double): String =
    if (percentage >= 90.0)
      "¡Excelente! Demuestras un dominio avanzado de la comunicación asertiva, escucha activa y colaboración en equipo. ¡Sigue inspirando a tu comunidad académica!"
    else if (percentage >= 70.0)
      "¡Muy Buen Desempeño! Cuentas con sólidas competencias en comunicación efectiva y resolución constructiva de conflictos. Aplica estas herramientas a diario en tus proyectos."
    else if (percentage >= 50.0)
      "Buen Intento. Comprendes nociones clave, pero se recomienda profundizar en las técnicas de asertividad ('Mensaje Yo') y netiqueta en canales asincrónicos."
    else
      "Oportunidad de Crecimiento. Te invitamos a repasar los artículos formativos de EduConnect sobre escucha activa y feedback constructivo para potenciar tu perfil profesional."
}

object QuizServiceLive {
  val live: ZLayer[QuizRepository, Nothing, QuizService] =
    ZLayer.fromFunction(QuizServiceLive.apply _)
}
