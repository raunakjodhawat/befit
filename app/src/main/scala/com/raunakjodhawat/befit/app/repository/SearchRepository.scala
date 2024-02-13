package com.raunakjodhawat.befit.app.repository

import com.raunakjodhawat.befit.dbschema.initialize.dbSetup
import com.raunakjodhawat.befit.dbschema.nutrientinformation.NutrientInformationTable
import slick.jdbc.PostgresProfile.api._
import zio._

class SearchRepository(dbZIO: ZIO[Any, Throwable, Database]) {

  private val nutrientInformation = dbSetup.nutrientInformationTable
  def searchByPrefix(
      prefix: String
  ): ZIO[Database, Throwable, Seq[
    NutrientInformationTable#TableElementType
  ]] = for {
    db <- dbZIO
    prefixMatches <- ZIO.fromFuture { ex =>
      db.run(
        nutrientInformation
          .filter(_.name.toLowerCase like s"%${prefix.toLowerCase}%")
          .take(10)
          .result
      )
    }
    _ <- ZIO.from(db.close())
  } yield prefixMatches
}
