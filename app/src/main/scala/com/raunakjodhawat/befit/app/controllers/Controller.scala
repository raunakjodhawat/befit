package com.raunakjodhawat.befit.app.controllers

import com.raunakjodhawat.befit.app.repository.SearchRepository
import slick.jdbc.PostgresProfile.api._
import zio._
import zio.http.{HttpApp, Response}
object Controller {
  def apply(db: ZIO[Any, Throwable, Database]): HttpApp[Database, Response] = {
    // val base_path: Path = Root / "api" / "v1"
    val searchRepository = new SearchRepository(db)

    val sc = new SearchController(searchRepository)

    sc.socketApp.toHttpApp
  }
}
