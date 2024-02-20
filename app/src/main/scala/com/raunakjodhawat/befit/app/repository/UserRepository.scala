package com.raunakjodhawat.befit.app.repository

import com.raunakjodhawat.befit.app.utility.Utility
import com.raunakjodhawat.befit.dbschema.initialize.dbSetup
import com.raunakjodhawat.befit.dbschema.user.{
  OutgoingCreateUser,
  User,
  UserTable
}
import zio.ZIO
import slick.jdbc.PostgresProfile.api._

class UserRepository(dbZIO: ZIO[Any, Throwable, Database]) {
  private val users: TableQuery[UserTable] = dbSetup.userTable
  def createUser(
      username: String,
      password: String
  ): ZIO[Database, Throwable, OutgoingCreateUser] = for {
    db <- dbZIO
    existingUser <- ZIO.fromFuture { ex =>
      {
        db.run(users.filter(_.username === username).result.headOption)
      }
    }
    _ <-
      if (existingUser.isDefined) {
        ZIO.fail(new Exception(s"User with $username already exists"))
      } else {
        ZIO.succeed(())
      }
    salt = Utility.getSalt
    hashedPassword = Utility.getHashedPassword(password, salt)
    user = User(0, username, salt, hashedPassword)
    newUserId <- ZIO.fromFuture { ex =>
      db.run(
        (users returning users.map(_.id)) += user
      )
    }
    _ <- ZIO.from(db.close())
  } yield OutgoingCreateUser(newUserId)

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

  def getUserById(id: Long): ZIO[Database, Throwable, OutgoingCreateUser] =
    for {
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
    } yield OutgoingCreateUser(user.id)

  def getByUsername(
      username: String
  ): ZIO[Database, Throwable, User] = for {
    db <- dbZIO
    userResult <- ZIO.fromFuture { ex =>
      db.run(
        users
          .filter(_.username === username)
          .result
          .headOption
      )
    }
    user <- ZIO
      .fromOption(userResult)
      .mapError(_ => new Exception(s"User with $username not found"))
    _ <- ZIO.from(db.close())
  } yield user

}
