import sbt.Keys._

lazy val root = (project in file(".")).
  enablePlugins(JmhPlugin).
  settings(
    name := "string-search-algos",
    version := "1.0",
    scalaVersion := "2.13.1"
  )