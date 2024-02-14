package com.raunakjodhawat.befit.app.repository

import zio.ZIO
import com.raunakjodhawat.befit.dbschema.initialize.dbSetup
import com.raunakjodhawat.befit.dbschema.nutrientinformation.NutrientInformation

import slick.jdbc.PostgresProfile.api._

class NutritionalInformationRepository(dbZIO: ZIO[Any, Throwable, Database]) {
  def createNewNutritionalInformation(
      name: String,
      protein: Option[Double],
      fat: Option[Double],
      carbs: Option[Double],
      unit: String,
      creator: Long
  ): ZIO[Database, Throwable, Int] = {
    val nutrientInformation = dbSetup.nutrientInformationTable
    for {
      db <- dbZIO
      insertResult <- ZIO.fromFuture { ex =>
        db.run(
          nutrientInformation += NutrientInformation(
            id = 1L,
            name = name,
            protein = protein,
            fat = fat,
            carbohydrate = carbs,
            unit = unit,
            creator = creator
          )
        )
      }
      result <-
        if (insertResult == 1) {
          ZIO.succeed(insertResult)
        } else {
          ZIO.fail(new Exception("Failed to insert"))
        }
      _ <- ZIO.from(db.close())
    } yield result
  }

  def getNutritionalInformationById(
      id: Long
  ): ZIO[Database, Throwable, Option[NutrientInformation]] = {
    val nutrientInformation = dbSetup.nutrientInformationTable
    for {
      db <- dbZIO
      result <- ZIO.fromFuture { ex =>
        db.run(
          nutrientInformation
            .filter(_.id === id)
            .result
            .headOption
        )
      }
      _ <- ZIO.from(db.close())
    } yield result
  }
}
