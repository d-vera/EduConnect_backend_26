package educonnect.repository

import java.util.UUID
import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.*
import educonnect.domain.{Article, ArticleSummary, Category}

trait ArticleRepository {
  def listCategories: Task[List[Category]]
  def findCategoryBySlug(slug: String): Task[Option[Category]]
  def listArticles(categorySlug: Option[String]): Task[List[ArticleSummary]]
  def findArticleBySlug(slug: String): Task[Option[Article]]
  def countArticles: Task[Long]
}

final case class ArticleRepositoryLive(quill: Quill.Postgres[SnakeCase]) extends ArticleRepository {
  import quill.*

  inline def categoriesSchema = quote {
    querySchema[Category]("categories")
  }

  inline def articlesSchema = quote {
    querySchema[Article]("articles")
  }

  override def listCategories: Task[List[Category]] =
    run(categoriesSchema.sortBy(_.name)(Ord.asc))

  override def findCategoryBySlug(slug: String): Task[Option[Category]] =
    run(categoriesSchema.filter(_.slug == lift(slug)))
      .map(_.headOption)

  override def listArticles(categorySlug: Option[String]): Task[List[ArticleSummary]] = {
    categorySlug match {
      case Some(cSlug) =>
        run {
          quote {
            for {
              c <- categoriesSchema if c.slug == lift(cSlug)
              a <- articlesSchema if a.categoryId == c.id
            } yield (a, c.name)
          }
        }.map { list =>
          list.map { case (a, catName) =>
            ArticleSummary(
              id = a.id,
              categoryId = a.categoryId,
              categoryName = catName,
              title = a.title,
              slug = a.slug,
              excerpt = a.excerpt,
              author = a.author,
              readTimeMinutes = a.readTimeMinutes,
              createdAt = a.createdAt
            )
          }
        }

      case None =>
        run {
          quote {
            for {
              a <- articlesSchema
              c <- categoriesSchema if a.categoryId == c.id
            } yield (a, c.name)
          }
        }.map { list =>
          list.map { case (a, catName) =>
            ArticleSummary(
              id = a.id,
              categoryId = a.categoryId,
              categoryName = catName,
              title = a.title,
              slug = a.slug,
              excerpt = a.excerpt,
              author = a.author,
              readTimeMinutes = a.readTimeMinutes,
              createdAt = a.createdAt
            )
          }
        }
    }
  }

  override def findArticleBySlug(slug: String): Task[Option[Article]] =
    run(articlesSchema.filter(_.slug == lift(slug)))
      .map(_.headOption)

  override def countArticles: Task[Long] =
    run(articlesSchema.size)
}

object ArticleRepositoryLive {
  val live: ZLayer[Quill.Postgres[SnakeCase], Nothing, ArticleRepository] =
    ZLayer.fromFunction(ArticleRepositoryLive.apply _)
}
