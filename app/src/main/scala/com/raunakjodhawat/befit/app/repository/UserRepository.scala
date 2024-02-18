package com.raunakjodhawat.befit.app.repository

import com.raunakjodhawat.befit.dbschema.initialize.dbSetup
import com.raunakjodhawat.befit.dbschema.user.{User, UserTable}
import slick.jdbc.PostgresProfile
import zio.ZIO
import slick.jdbc.PostgresProfile.api._

class UserRepository(dbZIO: ZIO[Any, Throwable, Database]) {
  private val users: TableQuery[UserTable] = dbSetup.userTable
  def createUser(id: Long): ZIO[Database, Throwable, User] = for {
    db <- dbZIO
    newUser <- ZIO.fromFuture { ex =>
      db.run(
        (users returning users)
          .+=(User(id = id))
      )
    }
    _ <- ZIO.from(db.close())
  } yield newUser

  def deleteUser(id: Long): ZIO[Database, Throwable, Unit] = {
    for {
      db <- dbZIO
      _ <- ZIO.fromFuture { ex =>
        db.run(
          users.filter(_.id === id).delete
        )
      }
      _ <- ZIO.from(db.close())
    } yield ()
  }

  def getUserById(id: Long): ZIO[Database, Throwable, User] = for {
    db <- dbZIO
    userResult <- ZIO.fromFuture { ex =>
      db.run(
        users.filter(_.id === id).result.headOption
      )
    }
    user <- ZIO
      .fromOption(userResult)
      .mapError(_ => new Exception(s"User with $id not found"))
    _ <- ZIO.from(db.close())
  } yield user

}
