package com.raunakjodhawat.befit.app.controllers

import com.raunakjodhawat.befit.dbschema.JsonEncoderDecoder._
import com.raunakjodhawat.befit.dbschema.UserHistoryResponseData
import com.raunakjodhawat.befit.dbschema.UserHistoryResponse
import com.raunakjodhawat.befit.dbschema.UserHistoryIncomingData
import com.raunakjodhawat.befit.dbschema.UserHistoryUpdateData

import com.raunakjodhawat.befit.app.repository.{
  NutritionalInformationRepository,
  UserHistoryRepository
}
import com.raunakjodhawat.befit.dbschema.user.JsonEncoderDecoder._
import io.circe.syntax.EncoderOps
import io.circe.parser.decode
import slick.jdbc.PostgresProfile.api._
import zio._
import zio.http._

class UserHistoryController(
    uhr: UserHistoryRepository,
    nir: NutritionalInformationRepository
) {
  def createHistory(
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
            uhr
              .createNewUserHistory(
                u_id = userHistory.u_id,
                ni_id = userHistory.ni_id,
                quantity = userHistory.quantity
              )
              .fold(
                _ => Response.status(Status.BadRequest),
                newHistory => Response.json(newHistory.asJson.toString())
              )
          }
        )
      )
  }

  def updateHistory(
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

  def getUserHistoryForADay(
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

  def getUserHistoryById(
      u_h_id: Long
  ): ZIO[Database, Throwable, Response] = {
    uhr
      .getUserHistoryById(u_h_id)
      .fold(
        _ => Response.status(Status.NotFound),
        userHistory => Response.json(userHistory.asJson.toString())
      )
  }

  def deleteHistoryByIdAndCreator(
      u_h_id: Long,
      u_id: Long
  ): ZIO[Database, Throwable, Response] = {
    uhr.deleteUserHistory(u_h_id, u_id) *> ZIO.succeed(
      Response.ok
    )
  }

}
