package com.mj.xiaomakuaibao

import java.time.LocalDateTime

import akka.actor.Actor
import better.files._
import com.mj.xiaomakuaibao.api.{NewsApi, TranslationApi}
import com.mj.xiaomakuaibao.utils.{DateUtils, FileUtils}

import scala.sys.process._
import scala.util.{Failure, Success, Try}
import FileUtils._

object Back extends Actor {
  val lastPublishedAtPath = "src/main/resources/lastPublishedAt.txt"

  override def receive: Receive = {
    case "com/mj/xiaomakuaibao" => {
      println(s"starting at ${LocalDateTime.now()} ...")
      run
    }
    case _ => throw new Exception("not allowed !")
  }

  def run: Unit = {
    val newsApi = NewsApi.apply()
    val translationApi = TranslationApi.apply()

    (for {
      lastPublishedAt <- readFile(lastPublishedAtPath)
      response <- newsApi.call
      (articles, newLastPublishedAt) <- Try(
        newsApi.filterNewsResponse(response, lastPublishedAt)
      )
      res <- translationApi.call(articles)
      _ <- Try(appendToFile("../source/_posts/xiaomakuaibao.md", res))
      _ <- runDeploy
      _ <- Try(overwriteToFile(lastPublishedAtPath, newLastPublishedAt))
    } yield Unit) match {
      case Success(_) => println("Yes !")
      case Failure(e) => throw new Exception(e)
    }

  }


  def runDeploy(): Try[String] = {
    Try("../deploy.sh".!!)
  }
}

object ScalaExtensions {

  implicit class RichTry[T](t: Try[T]) {
    def toEither: Either[Throwable, T] =
      t.transform(s => Success(Right(s)), e => Success(Left(e))).get
  }

  implicit class RichEither[L <: Throwable, R](e: Either[L, R]) {
    def toTry: Try[R] = e.fold(Failure(_), Success(_))
  }

}
