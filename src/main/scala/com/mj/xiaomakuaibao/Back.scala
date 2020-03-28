package com.mj.xiaomakuaibao

import java.time.LocalDateTime

import akka.actor.Actor
import com.mj.xiaomakuaibao.covid.Covid
import com.mj.xiaomakuaibao.news.News

import scala.sys.process._
import scala.util.{Failure, Success, Try}

object Back extends Actor {

  override def receive: Receive = {
    case "com/mj/xiaomakuaibao" => {
      println(s"starting at ${LocalDateTime.now()} ...")
      val news: Module = new News
      val covid: Module = new Covid
      covid :: news :: Nil foreach (_.run)
      deploy
    }
    case _ => throw new Exception("not allowed !")
  }

  def deploy: Unit = {
    "./deploy.sh".!!
    println("deployed !!")
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
