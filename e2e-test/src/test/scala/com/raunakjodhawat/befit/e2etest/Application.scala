package com.raunakjodhawat.befit.e2etest

import zio._
import zio.http._

import java.text.SimpleDateFormat
import java.util.Date

object Application extends ZIOAppDefault {
  val ss = new SearchSpec
  val program: ZIO[Client, Serializable, Unit] = for {
    _ <- UserSpec.runUserFlow
    _ <- ss.runWSSearchFlow
    u_id <- UserSpec.createUser
    _ <- NutritionalInformationSpec.runNutritionalInformationFlow(u_id)
    ni <- NutritionalInformationSpec.createNutritionalInformation(u_id)
    dateStr = new Date()
    sdf = new SimpleDateFormat("yyyy-MM-dd")
    _ <- HistorySpec.runHistoryFlow(u_id, ni.id, sdf.format(dateStr))
    _ <- UserSpec.deleteUserById(u_id)
  } yield ()
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    program.provide(Client.default, Scope.default)
}
