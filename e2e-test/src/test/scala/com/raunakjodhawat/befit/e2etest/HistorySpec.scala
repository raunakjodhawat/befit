package com.raunakjodhawat.befit.e2etest

import com.raunakjodhawat.befit.dbschema.{
  UserHistoryIncomingData,
  UserHistoryUpdateData
}
import com.raunakjodhawat.befit.dbschema.JsonEncoderDecoder._
import com.raunakjodhawat.befit.dbschema.user.JsonEncoderDecoder._
import com.raunakjodhawat.befit.dbschema.user.UserHistory
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import zio.ZIO
import zio.http.Client

object HistorySpec {
  def getHistoryByHistoryId(
      historyId: Long
  ): ZIO[Client, Throwable, UserHistory] = {
    for {
      client <- ZIO.service[Client]
      _ <- ZIO.succeed(println("Getting history"))
      uh <- client
        .url(createURL(basePath + s"/history/$historyId"))
        .get
        .mapError(error =>
          new Exception(s"Error getting history, ${error.getMessage}")
        )
      history <- uh.body.asString
        .map(decode[UserHistory])
        .flatMap(
          _.fold(
            error => ZIO.fail(error),
            uh => ZIO.succeed(uh)
          )
        )
      _ <- ZIO.succeed(
        println(s"Able to get history, with historyId = $historyId")
      )
    } yield history
  }
  def getHistoryByUserIdAndDate(
      userId: Long,
      date: String
  ): ZIO[Client, Throwable, Unit] = {
    for {
      client <- ZIO.service[Client]
      _ <- ZIO.succeed(println("Getting history"))
      _ <- client
        .url(createURL(basePath + s"/history/$userId/$date"))
        .get
        .mapError(error =>
          new Exception(s"Error getting history, ${error.getMessage}")
        )
      _ <- ZIO.succeed(
        println(s"Able to get history, with userId = $userId and date = $date")
      )
    } yield ()
  }

  def createHistory(
      u_id: Long,
      ni_id: Long,
      quantity: Double
  ): ZIO[Client, Throwable, UserHistory] = {
    for {
      client <- ZIO.service[Client]
      _ <- ZIO.succeed(println("Creating history"))
      historyData <- client
        .url(createURL(basePath + "/history"))
        .post(
          "",
          body = createBody(
            UserHistoryIncomingData(u_id, ni_id, quantity).asJson.toString
          )
        )
        .mapError(error =>
          new Exception(s"Error creating history, ${error.getMessage}")
        )
      h <- historyData.body.asString
        .map(decode[UserHistory])
        .flatMap(
          _.fold(error => ZIO.fail(error), h => ZIO.succeed(h))
        )
      _ <- ZIO.succeed(
        println(
          s"Able to create history, with u_id = $u_id, ni_id = $ni_id and quantity = $quantity"
        )
      )
    } yield h
  }

  def deleteHistory(
      h_id: Long,
      u_id: Long
  ): ZIO[Client, Throwable, Unit] = {
    for {
      client <- ZIO.service[Client]
      _ <- ZIO.succeed(println("Deleting history"))
      _ <- client
        .url(createURL(basePath + s"/history/$h_id/user/$u_id"))
        .delete
        .mapError(error =>
          new Exception(s"Error deleting history, ${error.getMessage}")
        )
      _ <- ZIO.succeed(
        println(
          s"Able to delete history, with h_id = $h_id and u_id = $u_id"
        )
      )
    } yield ()

  }

  def updateHistory(
      u_h_id: Long,
      u_id: Long,
      ni_id: Long,
      quantity: Double
  ): ZIO[Client, Throwable, Unit] = {
    for {
      client <- ZIO.service[Client]
      _ <- ZIO.succeed(println("Updating history"))
      _ <- client
        .url(createURL(basePath + "/history"))
        .put(
          "",
          body = createBody(
            UserHistoryUpdateData(u_h_id, u_id, ni_id, quantity).asJson.toString
          )
        )
        .mapError(error =>
          new Exception(s"Error updating history, ${error.getMessage}")
        )
      _ <- ZIO.succeed(
        println(
          s"Able to update history, with u_id = $u_id, ni_id = $ni_id and quantity = $quantity"
        )
      )
    } yield ()

  }

  def runHistoryFlow(
      u_id: Long,
      n_id: Long,
      dateStr: String
  ): ZIO[Client, Throwable, Unit] = {
    for {
      h <- createHistory(u_id, n_id, 100)
      _ <- getHistoryByUserIdAndDate(u_id, dateStr)
      _ <- getHistoryByHistoryId(h.id)
      _ <- updateHistory(h.id, u_id, n_id, 200)
      update_h <- getHistoryByHistoryId(h.id)
      _ <-
        if (update_h.qty == 200) ZIO.succeed(())
        else ZIO.fail(new Exception("History not updated"))
      _ <- deleteHistory(h.id, u_id)
      _ <- getHistoryByHistoryId(1).fold(
        _ => ZIO.succeed(()),
        _ => ZIO.fail(new Exception("History not deleted"))
      )
    } yield ()
  }
}
