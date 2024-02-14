ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"
lazy val dbSchemaModule = (project in file("db-schema"))
val zioVersion = "2.0.16"
val zioHttpVersion = "3.0.0-RC2"
val circeVersion = "0.14.5"

lazy val root = (project in file("."))
  .settings(
    name := "e2e-test",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-http" % zioHttpVersion,
      // json
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      // internal dependency
      "db-schema" %% "db-schema" % version.value
    )
  )
  .dependsOn(dbSchemaModule)
