package com.raunakjodhawat.dbloader.models
import slick.ast.TypedType
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, Rep, Tag}

class NutrientInformationTable(tag: Tag)
    extends Table[NutrientInformation](tag, "NUTRIENTINFORMATION") {

  def name: Rep[String] = column[String]("NAME")
  def protein: Rep[Double] = column[Double]("PROTEIN")
  def carbohydrate: Rep[Double] = column[Double]("CARBOHYDRATE")
  def fat: Rep[Double] = column[Double]("FAT")
  def unit: Rep[String] = column[String]("UNIT")
  override def * : ProvenShape[NutrientInformation] = {
    (name, protein, carbohydrate, fat, unit).mapTo[NutrientInformation]
  }
}
