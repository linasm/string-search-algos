import sbt.Keys._

lazy val root = (project in file(".")).
  enablePlugins(JmhPlugin).
  settings(
    name := "string-search-algos",
    version := "1.0",
    scalaVersion := "2.13.1",
    fork := true,
  )

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.1" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % "test"
