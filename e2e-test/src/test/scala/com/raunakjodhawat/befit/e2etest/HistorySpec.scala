package com.raunakjodhawat.befit.e2etest

import com.raunakjodhawat.befit.dbschema.user.UserHistory
import zio.ZIO
import zio.http.Client

object HistorySpec {
  def getHistoryByHistoryId(historyId: Long): ZIO[Client, Throwable, Unit] = {
    for {
      client <- ZIO.service[Client]
      _ <- ZIO.succeed(println("Getting history"))
      _ <- client
        .url(createURL(basePath + s"/history/$historyId"))
        .get
        .mapError(error =>
          new Exception(s"Error getting history, ${error.getMessage}")
        )
      _ <- ZIO.succeed(
        println(s"Able to get history, with historyId = $historyId")
      )
    } yield ()
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

}
