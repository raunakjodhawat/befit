ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"
val zioVersion = "2.0.16"
val zioHttpVersion = "3.0.0-RC2"
val slickVersion = "3.5.0-M4"
val circeVersion = "0.14.5"
lazy val dbSchemaModule = (project in file("db-schema"))
lazy val appModule = (project in file("."))
  .settings(
    name := "app",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-http" % zioHttpVersion,
      "com.typesafe.slick" %% "slick" % slickVersion,
      "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
      "org.postgresql" % "postgresql" % "42.5.4",
      // jwt token
      "com.github.jwt-scala" %% "jwt-zio-json" % "9.4.0",
      // zio test
      "dev.zio" %% "zio-test" % zioVersion % Test,
      "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
      "dev.zio" %% "zio-test-magnolia" % zioVersion % Test,
      "dev.zio" %% "zio-test-junit" % zioVersion % "test",
      "com.github.sbt" % "junit-interface" % "0.13.3" % Test,
      "org.scalatest" %% "scalatest" % "3.2.15" % Test,
      "org.scalatestplus" %% "junit-4-13" % "3.2.15.0" % Test,
      "info.senia" %% "zio-test-akka-http" % "2.0.4",
      // json
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      // internal dependency
      "db-schema" %% "db-schema" % version.value
    )
  )
  .dependsOn(dbSchemaModule)
