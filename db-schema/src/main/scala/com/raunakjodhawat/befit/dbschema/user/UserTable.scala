package com.raunakjodhawat.befit.dbschema.user

import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, Rep, Tag}
class UserTable(tag: Tag) extends Table[User](tag, "USER") {
  def id: Rep[Long] =
    column[Long]("ID", O.PrimaryKey, O.AutoInc)

  override def * : ProvenShape[User] = {
    (id).mapTo[User]
  }
}
