package com.raunakjodhawat.befit.app.controllers

import com.raunakjodhawat.befit.app.middleware.AuthMiddleware
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
    val ur = new UserRepository(db)
    val uc = new UserController(ur)

    val uhr = new UserHistoryRepository(db)
    val nir = new NutritionalInformationRepository(db)

    val am = new AuthMiddleware(ur)
    val uhc = new UserHistoryController(uhr, nir)
    val sr = new SearchRepository(db)
    val sc = new SearchController(sr)
    val nic = new NutritionalInformationController(nir)

    Http
      .collectZIO[Request] {
        case req @ Method.GET -> Root / "api" / "v1" / "user" / long(id) =>
          am.authenticator(req.headers) *> uc.getUser(id)
        case req @ Method.POST -> Root / "api" / "v1" / "user" =>
          uc.createUser(req.body)
        case req @ Method.GET -> Root / "api" / "v1" / "user" =>
          am.authenticator(req.headers)
        case Method.DELETE -> Root / "api" / "v1" / "user" / long(id) =>
          uc.deleteUserById(id)

        case Method.GET -> Root / "api" / "v1" / "history" / long(
              userId
            ) / date =>
          uhc.getUserHistoryForADay(userId, date)
        case Method.GET -> Root / "api" / "v1" / "history" / long(u_h_id) =>
          uhc.getUserHistoryById(u_h_id)
        case Method.DELETE -> Root / "api" / "v1" / "history" / long(
              u_h_id
            ) / "user" / long(userId) =>
          uhc.deleteHistoryByIdAndCreator(u_h_id, userId)
        case req @ Method.POST -> Root / "api" / "v1" / "history" =>
          uhc.createHistory(req.body)
        case req @ Method.PUT -> Root / "api" / "v1" / "history" =>
          uhc.updateHistory(req.body)

        case Method.GET -> Root / "api" / "v1" / "search" / "ws" =>
          sc.socketApp.toResponse

        case Method.GET -> Root / "api" / "v1" / "ni" / long(id) =>
          nic.getNutritionalInformationById(id)
        case Method.GET -> Root / "api" / "v1" / "ni" / "creator" / long(
              creator
            ) =>
          nic.getNutritionalInformationByCreator(creator)
        case req @ Method.POST -> Root / "api" / "v1" / "ni" =>
          nic.createNewNutritionalInformation(req.body)
        case req @ Method.PUT -> Root / "api" / "v1" / "ni" =>
          nic.updateNutritionalInformation(req.body)
        case Method.DELETE -> Root / "api" / "v1" / "ni" / long(
              id
            ) / "creator" / long(creator) =>
          nic.deleteNutritionalInformation(id, creator)
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
