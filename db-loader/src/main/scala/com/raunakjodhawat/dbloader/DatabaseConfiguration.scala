package com.raunakjodhawat.dbloader

import com.raunakjodhawat.dbloader.models.NutrientInformationValue
import com.raunakjodhawat.dbschema.initialize.dbSetup
import slick.jdbc.PostgresProfile.api._
import com.raunakjodhawat.dbschema.models.nutrientinformation.{
  NutrientInformation,
  NutrientInformationTable
}
import zio.ZIO

object DatabaseConfiguration extends App {

  private val mapping: Map[String, String] = Map(
    "Protein" -> "protein",
    "Carbohydrate, by difference" -> "carbohydrate",
    "Total lipid (fat)" -> "fat"
  )
  val nutrientInformationTable: TableQuery[NutrientInformationTable] =
    TableQuery[NutrientInformationTable]

  val dbEntry = RawFileReader.readFile(
    "/Users/raunakjodhawat/code/befit/db-loader/src/main/resources/raw.json",
    mapping
  )

  def insertData(
      dbEntry: Map[String, NutrientInformationValue]
  ): ZIO[Any, Throwable, Seq[Int]] = // Change the return type
    for {
      db <- dbSetup.initialize
      results <- ZIO.foreachPar(dbEntry) { case (key, value) =>
        ZIO
          .fromFuture { ec =>
            db.run(
              nutrientInformationTable += NutrientInformation(
                key,
                value.protein,
                value.carbohydrate,
                value.fat
              )
            )
          }
          .catchAll(e => ZIO.fail(e))
      }
    } yield results

}
