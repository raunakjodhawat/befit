ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

val slickVersion = "3.5.0-M4"
val circeVersion = "0.14.5"

lazy val dbSchemaModule = (project in file("db-schema"))

lazy val dbLoaderModule = (project in file("."))
  .settings(
    name := "db-loader",
    libraryDependencies ++= Seq(
      "com.typesafe.slick" %% "slick" % slickVersion,
      "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
      "org.postgresql" % "postgresql" % "42.5.4",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "dev.zio" %% "zio" % "2.1-RC1",
      "db-schema" %% "db-schema" % version.value
    ),
    Compile / unmanagedResourceDirectories += baseDirectory.value.getParentFile.getParentFile / "db-loader" / "src" / "main" / "resources"
  )
  .dependsOn(dbSchemaModule)
