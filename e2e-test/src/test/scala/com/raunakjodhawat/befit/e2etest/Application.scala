package com.raunakjodhawat.befit.e2etest

import zio._
import zio.http._

object Application extends ZIOAppDefault {
  val program: ZIO[Client, Serializable, Unit] = for {
    client <- ZIO.service[Client]
    _ <- UserSpec.runUserFlow(client)
  } yield ()
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    program.provide(Client.default, Scope.default)
}
