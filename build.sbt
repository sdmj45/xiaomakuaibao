import Dependencies._

ThisBuild / scalaVersion := "2.11.12"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.mj"
ThisBuild / organizationName := "xiaomakuaibao"
ThisBuild / name := "xiaomakuaibao"

lazy val root = (project in file("."))
  .settings(
    name := "back",
    libraryDependencies ++=
      scalaTest ++
        betterfile ++
        sttp ++
        json4s ++
        cats ++
        akka ++
        scalaCsv
  )
