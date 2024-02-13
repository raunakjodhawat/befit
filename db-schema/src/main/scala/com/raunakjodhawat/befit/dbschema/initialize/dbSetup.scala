package com.raunakjodhawat.befit.dbschema.initialize

import com.raunakjodhawat.befit.dbschema.nutrientinformation.NutrientInformationTable
import zio._
import slick.jdbc.PostgresProfile.api._

import scala.annotation.unused

@unused
object dbSetup {
  private val dbZIO = ZIO.attempt(Database.forConfig("postgres"))
  val nutrientInformationTable = TableQuery[NutrientInformationTable]

  @unused
  def initialize: ZIO[Any, Throwable, Database] = clearDB *> createDB
  private def createDB: ZIO[Any, Throwable, Database] = (for {
    db <- dbZIO
    updateFork <- ZIO.fromFuture { ex =>
      {
        db.run(
          DBIO.seq(
            nutrientInformationTable.schema.create
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
            nutrientInformationTable.schema.dropIfExists
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
