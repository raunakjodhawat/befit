ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val dbSchemaModule = (project in file("db-schema"))
  .settings(
    name := "db-schema"
  )
lazy val dbLoaderModule = (project in file("db-loader"))
  .settings(
    name := "db-loader"
  )
  .dependsOn(dbSchemaModule)

lazy val appModule = (project in file("app"))
  .settings(
    name := "app"
  )
  .dependsOn(dbSchemaModule)

lazy val root = (project in file("."))
  .settings(
    name := "befit",
    Compile / unmanagedResourceDirectories += baseDirectory.value.getParentFile.getParentFile / "db-loader" / "src" / "main" / "resources"
  )
  .dependsOn(dbSchemaModule, dbLoaderModule, appModule)
