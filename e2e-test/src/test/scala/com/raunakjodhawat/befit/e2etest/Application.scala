package com.raunakjodhawat.befit.e2etest

import zio._
import zio.http._

object Application extends ZIOAppDefault {
  val ss = new SearchSpec
  val program: ZIO[Client, Serializable, Unit] = for {
    _ <- UserSpec.runUserFlow // create a user, get user, delete user
    _ <- ss.runWSSearchFlow // get search results using websocket
    _ <-
      ss.runSearchFlow // create a user, create a nutritional information, search for ni, delete ni, search for it, delete the user
  } yield ()
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    program.provide(Client.default, Scope.default)
}
