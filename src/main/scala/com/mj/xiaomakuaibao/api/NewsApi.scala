package com.mj.xiaomakuaibao.api

import java.text.SimpleDateFormat
import java.time.{LocalDateTime, ZoneOffset}

import com.mj.xiaomakuaibao.model.{Article, NewsApiResponse}
import com.mj.xiaomakuaibao.utils.DateUtils
import org.json4s.jackson.JsonMethods.parse
import org.json4s.{DefaultFormats, _}
import sttp.client.{HttpURLConnectionBackend, basicRequest, _}

import scala.util.{Failure, Success, Try}

class NewsApi {

  def call: Try[NewsApiResponse] = {
    implicit val backend = HttpURLConnectionBackend()
    implicit val formats = new DefaultFormats {
      override def dateFormatter: SimpleDateFormat = DateUtils.formatter
    }

    val response = basicRequest
      .get(
        uri"http://newsapi.org/v2/top-headlines?sources=google-news-fr&apiKey=8408854764ab483a8783d193a0ead39e"
      )
      .send()

    response.body.fold(
      e => Failure(new Throwable(e)),
      r => Success(parse(r).extract[NewsApiResponse])
    )
  }

  def filterNewsResponse(
    res: NewsApiResponse,
    lastPublishedAt: LocalDateTime
  ): (List[Article], Option[String]) = {
    implicit val localDateOrdering: Ordering[LocalDateTime] =
      Ordering.by(_.toEpochSecond(ZoneOffset.UTC))

    val articles = res.articles.filter(
      article =>
        DateUtils
          .convertDateToLocalDatetime(article.publishedAt)
          .isAfter(lastPublishedAt)
    )
    articles match {
      case Nil => (List.empty[Article], None)
      case _ =>
        val lastDate = articles.map(_.publishedAt).max
        (articles, Some(DateUtils.formatter.format(lastDate)))
    }

  }
}

object NewsApi {
  def apply(): NewsApi = new NewsApi()
}
