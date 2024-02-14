package com.raunakjodhawat.befit.dbschema.nutrientinformation

import com.raunakjodhawat.befit.dbschema.initialize.dbSetup
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, Rep, Tag}

class NutrientInformationTable(tag: Tag)
    extends Table[NutrientInformation](tag, "NUTRIENTINFORMATION") {

  def id: Rep[Long] =
    column[Long]("ID", O.PrimaryKey, O.AutoInc)

  def name: Rep[String] = column[String]("S_NAME")
  def protein: Rep[Option[Double]] = column[Option[Double]]("PROTEIN")
  def carbohydrate: Rep[Option[Double]] = column[Option[Double]]("CARBOHYDRATE")
  def fat: Rep[Option[Double]] = column[Option[Double]]("FAT")
  def unit: Rep[String] = column[String]("UNIT")

  def creator: Rep[Long] = column[Long]("CREATOR")

  def creator_id_fk = foreignKey("USER_ID_FK", creator, dbSetup.userTable)(
    _.id,
    onUpdate = ForeignKeyAction.Restrict,
    onDelete = ForeignKeyAction.Cascade
  )
  override def * : ProvenShape[NutrientInformation] = {
    (id, name, protein, carbohydrate, fat, unit, creator)
      .mapTo[NutrientInformation]
  }
}
