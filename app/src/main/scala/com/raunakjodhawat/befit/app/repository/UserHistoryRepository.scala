package com.raunakjodhawat.befit.app.repository

import com.raunakjodhawat.befit.dbschema.initialize.dbSetup
import com.raunakjodhawat.befit.dbschema.user.UserHistory
import slick.jdbc.PostgresProfile.api._
import zio.ZIO

import java.text.SimpleDateFormat
import java.util.Date

class UserHistoryRepository(dbZIO: ZIO[Any, Throwable, Database]) {
  private val userHistoryTable = dbSetup.userHistoryTable
  def createNewUserHistory(
      u_id: Long,
      ni_id: Long,
      quantity: Double
  ): ZIO[Database, Throwable, UserHistory] = {
    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    for {
      db <- dbZIO
      result <-
        if (quantity > 0) {
          val date = sdf.format(new Date())
          val userHistory = UserHistory(0, u_id, ni_id, quantity, date)
          ZIO.fromFuture { ex =>
            db.run(
              userHistoryTable returning userHistoryTable += userHistory
            )
          }
        } else {
          ZIO.fail(new Exception("Quantity should be greater than 0"))
        }
      _ <- ZIO.from(db.close())
    } yield result
  }

  def updateUserHistory(
      u_h_id: Long,
      u_id: Long,
      ni_id: Long,
      quantity: Double
  ): ZIO[Database, Throwable, UserHistory] = for {
    db <- dbZIO
    _ <-
      if (quantity > 0) ZIO.fromFuture { ex =>
        db.run(
          userHistoryTable
            .filter(_.id === u_h_id)
            .filter(_.u_id === u_id)
            .filter(_.ni_id === ni_id)
            .map(x => (x.u_id, x.ni_id, x.qty))
            .update((u_id, ni_id, quantity))
        )
      }
      else {
        ZIO.fail(new Exception("Quantity should be greater than 0"))
      }
    updatedUserHistory <- ZIO
      .fromFuture { ex =>
        db.run(
          userHistoryTable
            .filter(x =>
              x.id === u_h_id && x.u_id === u_id && x.ni_id === ni_id
            )
            .result
            .headOption
        )
      }
      .flatMap {
        case Some(userHistory) => ZIO.succeed(userHistory)
        case None =>
          ZIO.fail(new Exception("Failed to retrieve updated UserHistory"))
      }
    _ <- ZIO.from(db.close())
  } yield updatedUserHistory

  def deleteUserHistory(
      u_h_id: Long,
      u_id: Long
  ): ZIO[Database, Throwable, Unit] = for {
    db <- dbZIO
    _ <- ZIO.fromFuture { ex =>
      db.run(
        userHistoryTable
          .filter(x => x.id === u_h_id && x.u_id === u_id)
          .delete
      )
    }
    _ <- ZIO.from(db.close())
  } yield ()

  def getUserHistoryByUserIdAndDate(
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

  def getUserHistoryById(
      u_h_id: Long
  ): ZIO[Database, Throwable, UserHistory] = for {
    db <- dbZIO
    result <- ZIO
      .fromFuture { ex =>
        db.run(
          dbSetup.userHistoryTable
            .filter(_.id === u_h_id)
            .result
            .head
        )
      }
    _ <- ZIO.from(db.close())
  } yield result
}
