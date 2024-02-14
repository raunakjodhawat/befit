package com.raunakjodhawat.befit.app.repository

import com.raunakjodhawat.befit.dbschema.initialize.dbSetup
import com.raunakjodhawat.befit.dbschema.user.UserHistory
import slick.jdbc.PostgresProfile.api._
import zio.ZIO

import java.text.SimpleDateFormat
import java.util.Date

class UserHistoryRepository(dbZIO: ZIO[Any, Throwable, Database]) {
  def createNewUserHistory(
      u_id: Long,
      ni_id: Long,
      quantity: Double
  ): ZIO[Database, Throwable, Int] = {
    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    for {
      db <- dbZIO
      result <-
        if (quantity > 0) ZIO.fromFuture { ex =>
          db.run(
            dbSetup.userHistoryTable += UserHistory(
              id = 1L,
              u_id = u_id,
              ni_id = ni_id,
              qty = quantity,
              updated_at = sdf.format(new Date())
            )
          )
        }
        else ZIO.fail(new Exception("Quantity should be greater than 0"))
      _ <- ZIO.from(db.close())
    } yield result
  }

  def updateUserHistory(
      u_h_id: Long,
      u_id: Long,
      ni_id: Long,
      quantity: Double
  ): ZIO[Database, Throwable, Int] = for {
    db <- dbZIO
    result <-
      if (quantity > 0) ZIO.fromFuture { ex =>
        db.run(
          dbSetup.userHistoryTable
            .filter(_.id === u_h_id)
            .filter(_.u_id === u_id)
            .filter(_.ni_id === ni_id)
            .map(_.qty)
            .update(quantity)
        )
      }
      else
        ZIO.fromFuture { ex =>
          db.run(
            dbSetup.userHistoryTable
              .filter(_.id === u_h_id)
              .filter(_.u_id === u_id)
              .filter(_.ni_id === ni_id)
              .delete
          )
        }
    _ <- ZIO.from(db.close())
  } yield result

  def getUserHistoryByUserIdByDate(
      u_id: Long,
      date: String
  ): ZIO[Database, Throwable, Seq[UserHistory]] = for {
    db <- dbZIO
    result <- ZIO
      .fromFuture { ex =>
        db.run(
          dbSetup.userHistoryTable
            .filter(_.u_id === u_id)
            .filter(_.updated_at === date)
            .result
        )
      }
    _ <- ZIO.from(db.close())
  } yield result
}
