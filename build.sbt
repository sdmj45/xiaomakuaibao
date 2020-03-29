import Dependencies._

ThisBuild / scalaVersion := Versions.scala
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.mj"
ThisBuild / organizationName := "xiaomakuaibao"
ThisBuild / name := "xiaomakuaibao"

enablePlugins(GhpagesPlugin)
git.remoteRepo := "git@github.com:sdmj45/majian.xiaomakuaibao.git"
ghpagesBranch := "master"
//ghpagesRepository := "public"

lazy val root = (project in file("."))
  .settings(
    name := "back",
    libraryDependencies ++=
      scalaLang ++
        scalaTest ++
        betterfile ++
        sttp ++
        json4s ++
        cats ++
        akka ++
        scalaCsv ++
        spark
  )
