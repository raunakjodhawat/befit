package com.raunakjodhawat.befit.dbschema.nutrientinformation

import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, Rep, Tag}

class NutrientInformationTable(tag: Tag)
    extends Table[NutrientInformation](tag, "NUTRIENTINFORMATION") {

  def name: Rep[String] = column[String]("S_NAME")
  def protein: Rep[Option[Double]] = column[Option[Double]]("PROTEIN")
  def carbohydrate: Rep[Option[Double]] = column[Option[Double]]("CARBOHYDRATE")
  def fat: Rep[Option[Double]] = column[Option[Double]]("FAT")
  override def * : ProvenShape[NutrientInformation] = {
    (name, protein, carbohydrate, fat).mapTo[NutrientInformation]
  }
}
