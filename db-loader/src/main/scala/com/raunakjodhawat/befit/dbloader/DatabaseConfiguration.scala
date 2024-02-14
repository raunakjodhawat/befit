package com.raunakjodhawat.befit.dbloader

import com.raunakjodhawat.befit.dbloader.models.NutrientInformationValue
import com.raunakjodhawat.befit.dbschema.initialize.dbSetup
import com.raunakjodhawat.befit.dbschema.nutrientinformation.NutrientInformation
import slick.jdbc.PostgresProfile.api._
import zio.{Unsafe, ZIO}

object Config {
  private val mapping: Map[String, String] = Map(
    "Protein" -> "protein",
    "Carbohydrate, by difference" -> "carbohydrate",
    "Total lipid (fat)" -> "fat"
  )

  val dbEntry: Map[String, NutrientInformationValue] = RawFileReader.readFile(
    "/Users/raunakjodhawat/code/befit/db-loader/src/main/resources/raw.json",
    mapping
  )
}
object DatabaseConfiguration extends App {

  def insertData(
      dbEntry: Map[String, NutrientInformationValue]
  ): ZIO[Any, Throwable, Unit] =
    for {
      db <- dbSetup.initialize
      dbEntries = dbEntry.map { case (key, value) =>
        val nutrientInfo =
          NutrientInformation(
            id = 1L,
            name = key,
            protein = value.protein,
            carbohydrate = value.carbohydrate,
            fat = value.fat,
            unit = value.unit,
            creator = 1L
          )
        db.run(dbSetup.nutrientInformationTable += nutrientInfo)
      }
      result <- ZIO.collectAllPar(
        dbEntries.map(dbEntry => ZIO.fromFuture(ex => dbEntry))
      )
      _ <- ZIO.succeed(result)
      _ <- dbSetup.closeDB(db)
    } yield ()

  Unsafe.unsafe { implicit unsafe =>
    zio.Runtime.default.unsafe
      .run(
        insertData(Config.dbEntry)
      )
      .getOrThrowFiberFailure()
  }
}
