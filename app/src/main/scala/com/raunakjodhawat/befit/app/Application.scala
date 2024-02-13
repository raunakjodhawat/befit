package com.raunakjodhawat.befit.app

import com.raunakjodhawat.befit.dbschema.initialize.dbSetup
import com.raunakjodhawat.befit.app.controllers.Controller
import slick.jdbc.PostgresProfile
import zio.http._
import zio._
import slick.jdbc.PostgresProfile.api._
import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

object Application extends ZIOAppDefault {
  val dbZIO: Task[PostgresProfile.backend.JdbcDatabaseDef] = dbSetup.dbZIO
  private val app: HttpApp[Database, Response] = Controller(dbZIO)

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    Server.serve(app).provide(Server.default, ZLayer.fromZIO(dbZIO))
}
