package com.raunakjodhawat.befit.dbschema.user

import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, Rep, Tag}
class UserTable(tag: Tag) extends Table[User](tag, "USER") {
  def id: Rep[Long] =
    column[Long]("ID", O.PrimaryKey, O.AutoInc)

  def username: Rep[String] =
    column[String]("USERNAME")

  def salt: Rep[String] =
    column[String]("SALT")

  def hashedPassword: Rep[String] =
    column[String]("HASHED_PASSWORD")
  override def * : ProvenShape[User] = {
    (id, username, salt, hashedPassword).mapTo[User]
  }
}
