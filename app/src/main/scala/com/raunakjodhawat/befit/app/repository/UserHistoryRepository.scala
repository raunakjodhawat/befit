package com.raunakjodhawat.befit.app.repository

import com.raunakjodhawat.befit.dbschema.initialize.dbSetup
import com.raunakjodhawat.befit.dbschema.user.JsonEncoderDecoder.sdf
import com.raunakjodhawat.befit.dbschema.user.UserHistory
import slick.jdbc.PostgresProfile.api._
import zio.ZIO

import java.util.Date

class UserHistoryRepository(dbZIO: ZIO[Any, Throwable, Database]) {
  def createNewUserHistory(
      u_id: Long,
      ni_id: Long,
      quantity: Double
  ): ZIO[Database, Throwable, Int] = for {
    db <- dbZIO
    result <-
      if (quantity > 0) ZIO.fromFuture { ex =>
        db.run(
          dbSetup.userHistoryTable += UserHistory(
            id = 1L,
            u_id = u_id,
            ni_id = ni_id,
            qty = quantity,
            created_at = Some(new Date()),
            updated_at = Some(new Date())
          )
        )
      }
      else ZIO.fail(new Exception("Quantity should be greater than 0"))
    _ <- ZIO.from(db.close())
  } yield result

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
      date: Date
  ): ZIO[Database, Throwable, Seq[UserHistory]] = for {
    db <- dbZIO
    result <- ZIO.fromFuture { ex =>
      db.run(
        dbSetup.userHistoryTable
          .filter(_.u_id === u_id)
          // .filter(x => sdf.format(x.created_at).substring(0, 10) === date)
          // todo: .filter(x => x.created_at === Some(date))
          .result
      )
    }
    _ <- ZIO.from(db.close())
  } yield result
}
