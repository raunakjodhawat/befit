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
    val ur = new UserRepository(db)
    val uc = new UserController(ur, base_path)

    val uhr = new UserHistoryRepository(db)
    val nir = new NutritionalInformationRepository(db)
    val uhc = new UserHistoryController(uhr, nir, base_path)

    val sr = new SearchRepository(db)

    val sc = new SearchController(sr)
    val nic = new NutritionalInformationController(nir)

    (uc.userRouter ++ uhc.userHistoryRouter).mapError(err =>
      Response(
        status = Status.BadRequest,
        headers = Headers(("Content-Type", "application/json")),
        body = Body.fromString(err.getMessage)
      )
    )

  }
}
