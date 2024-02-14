package com.raunakjodhawat.befit.app.repository

import com.raunakjodhawat.befit.dbschema.initialize.dbSetup
import com.raunakjodhawat.befit.dbschema.user.User
import zio.ZIO
import slick.jdbc.PostgresProfile.api._

class UserRepository(dbZIO: ZIO[Any, Throwable, Database]) {
  def createUser(id: Long): ZIO[Database, Throwable, Long] = {
    val user = dbSetup.userTable
    for {
      db <- dbZIO
      insertResult <- ZIO.fromFuture { ex =>
        db.run(
          (user returning user.map(_.id)) += User(
            id = id
          )
        )
      }
      _ <- ZIO.from(db.close())
    } yield insertResult
  }

  def deleteUser(id: Long): ZIO[Database, Throwable, Int] = {
    val user = dbSetup.userTable
    for {
      db <- dbZIO
      deleteResult <- ZIO.fromFuture { ex =>
        db.run(
          user.filter(_.id === id).delete
        )
      }
      _ <- ZIO.from(db.close())
    } yield deleteResult
  }

  def getUserById(id: Long): ZIO[Database, Throwable, User] = {
    val user = dbSetup.userTable
    for {
      db <- dbZIO
      userResult <- ZIO.fromFuture { ex =>
        db.run(
          user.filter(_.id === id).result.headOption
        )
      }
      user <- ZIO
        .fromOption(userResult)
        .mapError(_ => new Exception(s"User with $id not found"))
      _ <- ZIO.from(db.close())
    } yield user
  }
}
