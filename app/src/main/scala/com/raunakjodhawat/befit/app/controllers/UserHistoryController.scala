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
              u_h_id = userHistory.u_id,
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
        nir.getNutritionalInformationById(x.ni_id)
      )
      nutritionalInformation <- ZIO.collectAll(nutritionalInformationZIO)
    } yield {
      val qty = userHistory.map(_.qty)
      val proteinContent: Double = nutritionalInformation
        .flatMap(_.flatMap(_.protein))
        .sum
      val fatContent: Double = nutritionalInformation
        .flatMap(_.flatMap(_.fat))
        .sum
      val carbohydrateContent: Double = nutritionalInformation
        .flatMap(_.flatMap(_.carbohydrate))
        .sum
      val data: Seq[UserHistoryResponseData] = nutritionalInformation.flatMap(
        _.map(x =>
          UserHistoryResponseData(
            x.name,
            x.protein,
            x.carbohydrate,
            x.fat,
            x.unit
          )
        )
      )
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
