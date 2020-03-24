package com.mj.xiaomakuaibao.api

import cats.implicits._
import com.mj.xiaomakuaibao.model.{Article, TranslationResponse}
import com.mj.xiaomakuaibao.utils.DateUtils
import org.json4s.jackson.JsonMethods.parse
import org.json4s.{DefaultFormats, _}
import sttp.client.{HttpURLConnectionBackend, basicRequest, _}

import scala.util.{Failure, Success, Try}

class TranslationApi {

  import TranslationApi._

  def call(articles: List[Article]): Try[Option[String]] = {
    articles match {
      case Nil => Success(None)
      case _ =>
        articles
          .sortBy(_.publishedAt)
          .map(translateArticle _ andThen convertToMarkdown)
          .sequence
          .map(l => Some(l.mkString("\r\n")))
    }
  }

  def translateArticle(article: Article): Try[Article] = {
    /* val content = article.title :: article.description :: article.content :: Nil mkString separator
    callApi(content).map(res => {
      val Array(title, description, content) = res.split(separator)
      article.copy(title = title, description = description, content = content)
    })*/
    for {
      title <- callApi(article.title)
      description <- callApi(article.description)
      content <- callApi(article.content)
    } yield
      article.copy(title = title, description = description, content = content)
  }

  def callApi(body: String): Try[String] = {
    implicit val backend = HttpURLConnectionBackend()
    implicit val formats = DefaultFormats

    val response = basicRequest
      .post(
        uri"https://translate.yandex.net/api/v1/tr.json/translate?id=ce90dca4.5e7773e6.c35a6fac-0-0&srv=tr-text&lang=fr-zh&reason=auto&format=text"
      )
      .body(s"text=$body")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .send()

    response.body.fold(
      e => Failure(new Throwable(e)),
      r => Success(parse(r).extract[TranslationResponse].text.head)
    )
  }

  def convertToMarkdown: Try[Article] => Try[String] = _.map { article =>
    val newArticle = replaceChar(article)
    s"""
       |**标题:** **${newArticle.title}**
       |**描述:** ${newArticle.description}
       |**内容摘要:** ${newArticle.content}
       |**发布时间:** ${DateUtils.formatter.format(newArticle.publishedAt)}
       |**链接:** ${article.url}
       |**来源:** ${article.author}
       |""".stripMargin
  }

  private def replaceChar(article: Article): Article = {
    article.copy(
      title = replaceString(article.title),
      content = replaceString(article.content),
      description = replaceString(article.description)
    )
  }

}

object TranslationApi {
  val charsToReplace: Map[String, String] =
    Map("\\\\n" -> " ", "\\\\r" -> " ", "辆坦克" -> "个字符", "坦克" -> "字符")

  def apply(): TranslationApi = new TranslationApi()

  //  def separator: String = "\"££££££\""

  def replaceString(s: String): String =
    charsToReplace
      .foldLeft[String](s) {
        case (agg, (k, v)) => agg.replace(k, v)
      }
      .replaceAll("<[^>]*>", "")
}
