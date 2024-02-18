package com.raunakjodhawat.befit.app.controllers

import com.raunakjodhawat.befit.app.models.{
  UserHistoryIncomingData,
  UserHistoryResponse,
  UserHistoryResponseData,
  UserHistoryUpdateData
}
import com.raunakjodhawat.befit.app.repository.{
  NutritionalInformationRepository,
  UserHistoryRepository
}
import com.raunakjodhawat.befit.app.models.JsonEncoderDecoder._
import com.raunakjodhawat.befit.dbschema.user.JsonEncoderDecoder._
import io.circe.syntax.EncoderOps
import io.circe.parser.decode
import slick.jdbc.PostgresProfile.api._
import zio._
import zio.http._

class UserHistoryController(
    uhr: UserHistoryRepository,
    nir: NutritionalInformationRepository,
    basePath: Path
) {
  val userHistoryRouter: Http[Database, Throwable, Request, Response] = Http
    .collectZIO[Request] {
      case Method.GET -> basePath / "history" / long(
            userId
          ) / "history" / date =>
        getUserHistoryForADay(userId, date)
      case Method.GET -> basePath / "history" / long(u_h_id) =>
        getUserHistoryById(u_h_id)
      case Method.DELETE -> basePath / "history" / long(
            u_h_id
          ) / "user" / long(userId) =>
        deleteHistoryByIdAndCreator(u_h_id, userId)
      case req @ Method.POST -> basePath / "history" =>
        createHistory(req.body)
      case req @ Method.PUT -> basePath / "history" =>
        updateHistory(req.body)
    }
  private def createHistory(
      body: Body
  ): ZIO[Database, Throwable, Response] = {
    body.asString
      .map(decode[UserHistoryIncomingData])
      .flatMap(
        _.fold(
          error => {
            ZIO.fail(new Exception(s"Error decoding, ${error.getMessage}"))
          },
          userHistory => {
            uhr.createNewUserHistory(
              u_id = userHistory.u_id,
              ni_id = userHistory.ni_id,
              quantity = userHistory.quantity
            ) *> ZIO.succeed(Response.json(userHistory.asJson.toString()))
          }
        )
      )
  }

  private def updateHistory(
      body: Body
  ): ZIO[Database, Throwable, Response] = {
    body.asString
      .map(decode[UserHistoryUpdateData])
      .flatMap(
        _.fold(
          error => {
            ZIO.fail(new Exception(s"Error decoding, ${error.getMessage}"))
          },
          userHistory => {
            uhr.updateUserHistory(
              u_h_id = userHistory.u_h_id,
              u_id = userHistory.u_id,
              ni_id = userHistory.ni_id,
              quantity = userHistory.quantity
            ) *> ZIO.succeed(Response.json(userHistory.asJson.toString()))
          }
        )
      )
  }

  private def getUserHistoryForADay(
      userId: Long,
      date: String
  ): ZIO[Database, Throwable, Response] = {
    for {
      userHistory <- uhr.getUserHistoryByUserIdAndDate(userId, date)
      nutritionalInformationZIO = userHistory.map(x =>
        nir.getNutritionalInformationById(x.ni_id) -> x.qty
      )
      (infoZIOs, quantities) = nutritionalInformationZIO.unzip
      nutritionalInformation <- ZIO.collectAll(infoZIOs)
      nutritionalInformationWithQty = nutritionalInformation.zip(quantities)
    } yield {
      val proteinContent: Double = nutritionalInformationWithQty.flatMap {
        case (info, qty) => info.protein.map(_ * qty)
      }.sum
      val fatContent: Double = nutritionalInformationWithQty.flatMap {
        case (info, qty) => info.fat.map(_ * qty)
      }.sum
      val carbohydrateContent: Double = nutritionalInformationWithQty.flatMap {
        case (info, qty) => info.carbohydrate.map(_ * qty)
      }.sum
      val data: Seq[UserHistoryResponseData] =
        nutritionalInformationWithQty.map { case (info, qty) =>
          UserHistoryResponseData(
            name = info.name,
            protein = info.protein,
            carbohydrate = info.carbohydrate,
            fat = info.fat,
            quantity = qty,
            unit = info.unit
          )
        }
      val response = UserHistoryResponse(
        proteinContent,
        carbohydrateContent,
        fatContent,
        data
      )
      Response.json(response.asJson.toString())
    }
  }

  private def getUserHistoryById(
      u_h_id: Long
  ): ZIO[Database, Throwable, Response] = {
    uhr.getUserHistoryById(u_h_id).map(x => Response.json(x.asJson.toString()))
  }

  private def deleteHistoryByIdAndCreator(
      u_h_id: Long,
      u_id: Long
  ): ZIO[Database, Throwable, Response] = {
    uhr.deleteUserHistory(u_h_id, u_id) *> ZIO.succeed(
      Response.ok
    )
  }

}
