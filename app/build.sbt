import scala.collection.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val appModule = (project in file("."))
  .settings(
    name := "app"
  )
