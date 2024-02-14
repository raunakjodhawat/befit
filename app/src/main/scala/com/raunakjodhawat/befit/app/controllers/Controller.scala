package com.raunakjodhawat.befit.app.controllers

import com.raunakjodhawat.befit.app.repository.{
  NutritionalInformationRepository,
  SearchRepository,
  UserRepository
}
import slick.jdbc.PostgresProfile.api._
import zio._
import zio.http._

object Controller {
  def apply(db: ZIO[Any, Throwable, Database]): HttpApp[Database, Response] = {
    val base_path: Path = Root / "api" / "v1"
    val sr = new SearchRepository(db)
    val nis = new NutritionalInformationRepository(db)
    var ur = new UserRepository(db)
    Http
      .collectZIO[Request] {
        case Method.GET -> base_path / "search" / "ws" =>
          new SearchController(sr).socketApp.toResponse
        case Method.GET -> base_path / "search" / long(id) =>
          new SearchController(sr).searchById(id)
        case req @ Method.POST -> base_path / "create" / "user" =>
          new UserController(ur).createUser(req.body)
        case req @ Method.POST -> base_path / "create" / "food" =>
          new NutritionalInformationController(nis)
            .createNewNutritionalInformation(req.body)
      }
      .mapError(err =>
        Response(
          status = Status.BadRequest,
          headers = Headers(("Content-Type", "application/json")),
          body = Body.fromString(err.getMessage)
        )
      )
  }
}
