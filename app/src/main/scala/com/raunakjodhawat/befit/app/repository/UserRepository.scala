package com.raunakjodhawat.befit.app.repository

import com.raunakjodhawat.befit.dbschema.initialize.dbSetup
import com.raunakjodhawat.befit.dbschema.user.User
import zio.ZIO
import slick.jdbc.PostgresProfile.api._

class UserRepository(dbZIO: ZIO[Any, Throwable, Database]) {
  def createUser(id: Long): ZIO[Database, Throwable, Int] = {
    val user = dbSetup.userTable
    for {
      db <- dbZIO
      insertResult <- ZIO.fromFuture { ex =>
        db.run(
          user += User(
            id = id
          )
        )
      }
      result <-
        if (insertResult == 1) {
          ZIO.succeed(insertResult)
        } else {
          ZIO.fail(new Exception("Failed to insert"))
        }
      _ <- ZIO.from(db.close())
    } yield result
  }
}
