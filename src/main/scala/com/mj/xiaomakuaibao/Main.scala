package com.mj.xiaomakuaibao

import akka.actor.Props

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Main {
  def main(args: Array[String]): Unit = {
    val system = akka.actor.ActorSystem("system")
    val backJob = system.actorOf(Props(Back), "back-job")

    system.scheduler.schedule(0 millisecond, 10 second, backJob, "com/mj/xiaomakuaibao")
  }
}
