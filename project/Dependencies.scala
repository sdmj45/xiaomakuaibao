import sbt._

object Dependencies {
  val jacksonExclusions = Seq(
    ExclusionRule(organization = "com.fasterxml.jackson.core"),
    ExclusionRule(organization = "org.codehaus.jackson")
  )
  lazy val scalaTest = Seq("org.scalatest" %% "scalatest" % "3.0.5" % Test)
  lazy val betterfile = Seq("com.github.pathikrit" %% "better-files" % "3.8.0")
  lazy val sttp = Seq("com.softwaremill.sttp.client" %% "core" % "2.0.3")
  lazy val jackson = Seq(
    "com.fasterxml.jackson.core" % "jackson-databind" % Versions.jackson,
    "com.fasterxml.jackson.core" % "jackson-core" % Versions.jackson,
    "com.fasterxml.jackson.core" % "jackson-annotations" % Versions.jackson,
    "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % Versions.jackson
  )
  lazy val json4s = Seq(
    "org.json4s" %% "json4s-jackson" % Versions.json4s,
    "org.json4s" %% "json4s-ext" % Versions.json4s
  ).map(_.excludeAll(jacksonExclusions: _*)) ++ jackson
  lazy val cats = Seq("org.typelevel" %% "cats-core" % "2.0.0")
  lazy val akka = Seq("com.typesafe.akka" %% "akka-actor-typed" % "2.5.21")
  lazy val scalaCsv = Seq("com.github.tototoshi" %% "scala-csv" % "1.3.6")
}

object Versions {
  lazy val json4s: String = "3.7.0-M2"
  lazy val jackson: String = "2.10.3"
}
