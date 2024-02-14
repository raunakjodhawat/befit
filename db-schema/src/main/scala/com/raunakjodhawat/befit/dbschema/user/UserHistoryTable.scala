package com.raunakjodhawat.befit.dbschema.user

import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, Rep, Tag}

import com.raunakjodhawat.befit.dbschema.initialize.dbSetup

class UserHistoryTable(tag: Tag)
    extends Table[UserHistory](tag, "USERHISTORY") {
  def id: Rep[Long] =
    column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def u_id: Rep[Long] = column[Long]("U_ID")

  def user_id_fk = foreignKey("USER_ID_FK", u_id, dbSetup.userTable)(
    _.id,
    onUpdate = ForeignKeyAction.Restrict,
    onDelete = ForeignKeyAction.Cascade
  )

  def ni_id: Rep[Long] = column[Long]("NI_ID")

  def nutrient_information_id_fk = foreignKey(
    "NUTRIENT_INFORMATION_ID_FK",
    ni_id,
    dbSetup.nutrientInformationTable
  )(
    _.id,
    onUpdate = ForeignKeyAction.Restrict,
    onDelete = ForeignKeyAction.Cascade
  )
  def qty: Rep[Double] = column[Double]("QTY")
  def updated_at: Rep[String] = column[String]("UPDATED_AT")
  override def * : ProvenShape[UserHistory] = {
    (id, u_id, ni_id, qty, updated_at).mapTo[UserHistory]
  }
}
