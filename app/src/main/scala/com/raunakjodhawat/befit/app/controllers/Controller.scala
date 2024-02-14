package com.raunakjodhawat.befit.app.controllers

import com.raunakjodhawat.befit.app.repository.{
  NutritionalInformationRepository,
  SearchRepository,
  UserHistoryRepository,
  UserRepository
}
import slick.jdbc.PostgresProfile.api._
import zio._
import zio.http._

object Controller {
  def apply(db: ZIO[Any, Throwable, Database]): HttpApp[Database, Response] = {
    val base_path: Path = Root / "api" / "v1"
    val sr = new SearchRepository(db)
    val nir = new NutritionalInformationRepository(db)
    val ur = new UserRepository(db)
    val uhr = new UserHistoryRepository(db)
    Http
      .collectZIO[Request] {
        case Method.GET -> base_path / "search" / "ws" =>
          new SearchController(sr).socketApp.toResponse
        case Method.GET -> base_path / "search" / long(id) =>
          new SearchController(sr).searchById(id)
        case req @ Method.POST -> base_path / "create" / "user" =>
          new UserController(ur).createUser(req.body)

        case req @ Method.POST -> base_path / "history" =>
          new UserHistoryController(uhr, nir).createNewUserHistory(req.body)
        case req @ Method.PUT -> base_path / "history" =>
          new UserHistoryController(uhr, nir).updateUserHistory(req.body)
        case Method.GET -> base_path / "history" / long(id) / date =>
          new UserHistoryController(uhr, nir).getUserHistoryForADay(id, date)

        case req @ Method.POST -> base_path / "create" / "food" =>
          new NutritionalInformationController(nir)
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
