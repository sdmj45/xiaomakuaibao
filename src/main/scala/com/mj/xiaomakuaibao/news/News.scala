package com.mj.xiaomakuaibao.news

import com.mj.xiaomakuaibao.Module
import com.mj.xiaomakuaibao.news.api.{NewsApi, TranslationApi}
import com.mj.xiaomakuaibao.utils.FileUtils.{appendToFile, overwriteToFile, readFile}

import scala.util.{Failure, Success, Try}

class News extends Module {
  override val lastUpdatedFile: String = "data/last_updated/news_last_updated.txt"

  override def run: Unit = {
    val newsApi = NewsApi.apply()
    val translationApi = TranslationApi.apply()

    (for {
      lastPublishedAt <- readFile(lastUpdatedFile)
      response <- newsApi.call
      (articles, newLastPublishedAt) <- Try(
        newsApi.filterNewsResponse(response, lastPublishedAt)
      )
      res <- translationApi.call(articles)
      _ <- Try(appendToFile("public/assets/data/news.html", res))
      _ <- Try(overwriteToFile(lastUpdatedFile, newLastPublishedAt))
    } yield Unit) match {
      case Success(_) => println("News ok !")
      case Failure(e) => throw new Exception(e)
    }
  }
}
