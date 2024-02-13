package com.raunakjodhawat.befit.app.controllers

import com.raunakjodhawat.befit.app.repository.{
  NutritionalInformationRepository,
  SearchRepository
}
import slick.jdbc.PostgresProfile.api._
import zio._
import zio.http._
import zio.prelude.data.Optional.AllValuesAreNullable

object Controller {
  def apply(db: ZIO[Any, Throwable, Database]): HttpApp[Database, Response] = {
    val base_path: Path = Root / "api" / "v1"
    val sr = new SearchRepository(db)
    val nis = new NutritionalInformationRepository(db)

    Http
      .collectZIO[Request] {
        case Method.GET -> base_path / "ws" / "search" =>
          new SearchController(sr).socketApp.toResponse
        case req @ Method.POST -> base_path / "create" =>
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
