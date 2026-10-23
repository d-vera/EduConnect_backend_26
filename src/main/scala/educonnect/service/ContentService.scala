package educonnect.service

import zio.*
import educonnect.domain.*
import educonnect.repository.ArticleRepository

trait ContentService {
  def getCategories: IO[ApiError, List[Category]]
  def getArticles(categorySlug: Option[String]): IO[ApiError, List[ArticleSummary]]
  def getArticleBySlug(slug: String): IO[ApiError, Article]
}

final case class ContentServiceLive(repo: ArticleRepository) extends ContentService {

  override def getCategories: IO[ApiError, List[Category]] =
    repo.listCategories
      .mapError(e => DatabaseError(s"Error retrieving categories: ${e.getMessage}"))

  override def getArticles(categorySlug: Option[String]): IO[ApiError, List[ArticleSummary]] =
    categorySlug match {
      case Some(slug) =>
        repo.findCategoryBySlug(slug)
          .mapError(e => DatabaseError(e.getMessage))
          .flatMap {
            case None => ZIO.fail(NotFoundError(s"Categoría con slug '$slug' no encontrada"))
            case Some(_) =>
              repo.listArticles(Some(slug))
                .mapError(e => DatabaseError(s"Error retrieving articles: ${e.getMessage}"))
          }
      case None =>
        repo.listArticles(None)
          .mapError(e => DatabaseError(s"Error retrieving articles: ${e.getMessage}"))
    }

  override def getArticleBySlug(slug: String): IO[ApiError, Article] =
    repo.findArticleBySlug(slug)
      .mapError(e => DatabaseError(s"Error retrieving article '$slug': ${e.getMessage}"))
      .flatMap {
        case Some(article) => ZIO.succeed(article)
        case None => ZIO.fail(NotFoundError(s"Artículo con slug '$slug' no encontrado"))
      }
}

object ContentServiceLive {
  val live: ZLayer[ArticleRepository, Nothing, ContentService] =
    ZLayer.fromFunction(ContentServiceLive.apply _)
}
