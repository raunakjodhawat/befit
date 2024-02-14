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

import zio.ZIO
import zio.http.{Body, Response}
import io.circe.syntax.EncoderOps
import io.circe.parser.decode
import slick.jdbc.PostgresProfile.api._
class UserHistoryController(
    uhr: UserHistoryRepository,
    nir: NutritionalInformationRepository
) {
  def createNewUserHistory(
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
            )
          }
        )
      ) *> ZIO.succeed(
      Response.ok
    )

  }

  def updateUserHistory(
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
            )
          }
        )
      ) *> ZIO.succeed(
      Response.ok
    )
  }

  def getUserHistoryForADay(
      userId: Long,
      date: String
  ): ZIO[Database, Throwable, Response] = {
    for {
      userHistory <- uhr.getUserHistoryByUserIdByDate(userId, date)
      nutritionalInformationZIO = userHistory.map(x =>
        nir.getNutritionalInformationById(x.ni_id) -> x.qty
      )
      (infoZIOs, quantities) = nutritionalInformationZIO.unzip
      nutritionalInformation <- ZIO.collectAll(infoZIOs)
      nutritionalInformationWithQty = nutritionalInformation.zip(quantities)
    } yield {
      val proteinContent: Double = nutritionalInformationWithQty.flatMap {
        case (info, qty) => info.flatMap(_.protein).map(_ * qty)
      }.sum
      val fatContent: Double = nutritionalInformationWithQty.flatMap {
        case (info, qty) => info.flatMap(_.fat).map(_ * qty)
      }.sum
      val carbohydrateContent: Double = nutritionalInformationWithQty.flatMap {
        case (info, qty) => info.flatMap(_.carbohydrate).map(_ * qty)
      }.sum
      val data: Seq[UserHistoryResponseData] =
        nutritionalInformationWithQty.flatMap { case (info, qty) =>
          info.map(x =>
            UserHistoryResponseData(
              name = x.name,
              protein = x.protein,
              carbohydrate = x.carbohydrate,
              fat = x.fat,
              quantity = qty,
              unit = x.unit
            )
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

}
