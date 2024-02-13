ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

val slickVersion = "3.5.0-M4"
val postgresVersion = "42.5.4"
val zioVersion = "2.1-RC1"
val zioHttpVersion = "3.0.0-RC2"
val circeVersion = "0.14.5"
lazy val dbSchemaModule = (project in file("."))
  .settings(
    name := "db-schema",
    libraryDependencies ++= Seq(
      "com.typesafe.slick" %% "slick" % slickVersion,
      "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
      "org.postgresql" % "postgresql" % postgresVersion,
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-http" % zioHttpVersion,
      // json
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion
    )
  )
