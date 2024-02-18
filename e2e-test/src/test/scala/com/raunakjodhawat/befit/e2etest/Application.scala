package com.raunakjodhawat.befit.e2etest

import zio._
import zio.http._

object Application extends ZIOAppDefault {
  val ss = new SearchSpec
  val program: ZIO[Client, Serializable, Unit] = for {
    _ <- UserSpec.runUserFlow
    _ <- ss.runSearchFlow
  } yield ()
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    program.provide(Client.default, Scope.default)
}
