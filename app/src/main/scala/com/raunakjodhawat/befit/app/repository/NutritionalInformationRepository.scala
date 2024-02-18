package com.raunakjodhawat.befit.app.repository

import zio.ZIO
import com.raunakjodhawat.befit.dbschema.initialize.dbSetup
import com.raunakjodhawat.befit.dbschema.nutrientinformation.NutrientInformation

import slick.jdbc.PostgresProfile.api._

class NutritionalInformationRepository(dbZIO: ZIO[Any, Throwable, Database]) {
  val nutrientInformation = dbSetup.nutrientInformationTable
  def createNewNutritionalInformation(
      name: String,
      protein: Option[Double],
      fat: Option[Double],
      carbs: Option[Double],
      unit: String,
      creator: Long
  ): ZIO[Database, Throwable, NutrientInformation] = {
    for {
      db <- dbZIO
      newNutrientInformation <- ZIO
        .fromFuture { ex =>
          db.run(
            (nutrientInformation returning nutrientInformation)
              .+=(
                NutrientInformation(
                  id = 0,
                  name = name,
                  protein = protein,
                  fat = fat,
                  carbohydrate = carbs,
                  unit = unit,
                  creator = creator
                )
              )
          )
        }
      _ <- ZIO.from(db.close())
    } yield newNutrientInformation
  }

  def getNutritionalInformationById(
      id: Long
  ): ZIO[Database, Throwable, NutrientInformation] = {
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
      nutrientInformation <- ZIO
        .fromOption(result)
        .mapError(_ =>
          new Exception(s"Nutrient Information with $id not found")
        )
    } yield nutrientInformation
  }

  def getNutritionalInformationByCreator(
      creator: Long
  ): ZIO[Database, Throwable, Seq[NutrientInformation]] = {
    for {
      db <- dbZIO
      result <- ZIO.fromFuture { ex =>
        db.run(
          nutrientInformation
            .filter(_.creator === creator)
            .take(100)
            .result
        )
      }
      _ <- ZIO.from(db.close())
    } yield result
  }
  def deleteNutritionalInformationByIdAndCreator(
      id: Long,
      creator: Long
  ): ZIO[Database, Throwable, Unit] = for {
    db <- dbZIO
    _ <- ZIO.fromFuture { ex =>
      db.run(
        nutrientInformation
          .filter(_.id === id)
          .filter(_.creator === creator)
          .delete
      )
    }
    _ <- ZIO.from(db.close())
  } yield ()
}
