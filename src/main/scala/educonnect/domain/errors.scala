package educonnect.domain

import zio.json.*
import sttp.tapir.Schema

sealed trait ApiError {
  def message: String
}

object ApiError {
  given JsonCodec[ApiError] = DeriveJsonCodec.gen[ApiError]
  given Schema[ApiError] = Schema.derived[ApiError]
}

final case class NotFoundError(message: String) extends ApiError
object NotFoundError {
  given JsonCodec[NotFoundError] = DeriveJsonCodec.gen[NotFoundError]
  given Schema[NotFoundError] = Schema.derived[NotFoundError]
}

final case class ValidationError(message: String, field: Option[String] = None) extends ApiError
object ValidationError {
  given JsonCodec[ValidationError] = DeriveJsonCodec.gen[ValidationError]
  given Schema[ValidationError] = Schema.derived[ValidationError]
}

final case class InternalError(message: String) extends ApiError
object InternalError {
  given JsonCodec[InternalError] = DeriveJsonCodec.gen[InternalError]
  given Schema[InternalError] = Schema.derived[InternalError]
}

final case class DatabaseError(message: String) extends ApiError
object DatabaseError {
  given JsonCodec[DatabaseError] = DeriveJsonCodec.gen[DatabaseError]
  given Schema[DatabaseError] = Schema.derived[DatabaseError]
}
