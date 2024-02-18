package com.raunakjodhawat.befit.dbschema.initialize

import com.raunakjodhawat.befit.dbschema.nutrientinformation.NutrientInformationTable
import com.raunakjodhawat.befit.dbschema.user.{
  User,
  UserHistoryTable,
  UserTable
}
import slick.jdbc.PostgresProfile
import zio._
import slick.jdbc.PostgresProfile.api._

import scala.annotation.unused
import scala.util.Properties

@unused
object dbSetup {
  val dbZIO: Task[PostgresProfile.backend.JdbcDatabaseDef] =
    ZIO.attempt(Database.forConfig(Properties.envOrElse("DBPATH", "postgres")))
  val nutrientInformationTable: TableQuery[NutrientInformationTable] =
    TableQuery[NutrientInformationTable]
  val userTable: TableQuery[UserTable] =
    TableQuery[UserTable]
  val userHistoryTable: TableQuery[UserHistoryTable] =
    TableQuery[UserHistoryTable]
  private val adminUser: User = User(1L)

  @unused
  def initialize: ZIO[Any, Throwable, Database] = clearDB *> createDB
  private def createDB: ZIO[Any, Throwable, Database] = (for {
    db <- dbZIO
    updateFork <- ZIO.fromFuture { ex =>
      {
        db.run(
          DBIO.seq(
            userTable.schema.create,
            nutrientInformationTable.schema.create,
            userHistoryTable.schema.create,
            userTable += adminUser
          )
        )
      }
    }.fork
    dbUpdateResult <- updateFork.await
  } yield dbUpdateResult match {
    case Exit.Success(_) =>
      ZIO.succeed(println("Database Initialization complete")) *> ZIO.from(db)
    case Exit.Failure(cause) =>
      ZIO.succeed(
        println(s"Database Initialization errored, ${cause.failures}")
      ) *> ZIO.fail(
        new Exception("Failed to initialize")
      )
  }).flatMap(x => x)
  private def clearDB: ZIO[Any, Throwable, Database] = (for {
    db <- dbZIO
    updateFork <- ZIO.fromFuture { ex =>
      {
        db.run(
          DBIO.seq(
            userHistoryTable.schema.dropIfExists,
            nutrientInformationTable.schema.dropIfExists,
            userTable.schema.dropIfExists
          )
        )
      }
    }.fork
    dbUpdateResult <- updateFork.await
  } yield dbUpdateResult match {
    case Exit.Success(_) =>
      ZIO.succeed(println("Database cleared")) *> ZIO.from(db)
    case Exit.Failure(cause) =>
      ZIO.succeed(
        println(s"Database clearing error, ${cause.failures}")
      ) *> ZIO.fail(
        new Exception("Failed to clear the DB")
      )
  }).flatMap(x => x)
  @unused
  def closeDB(db: Database): ZIO[Any, Throwable, Unit] =
    ZIO.attempt(db.close)
}
