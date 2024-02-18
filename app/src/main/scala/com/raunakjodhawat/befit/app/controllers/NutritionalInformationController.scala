package com.raunakjodhawat.befit.app.controllers

import com.raunakjodhawat.befit.app.repository.NutritionalInformationRepository
import com.raunakjodhawat.befit.dbschema.nutrientinformation.NutrientInformation
import com.raunakjodhawat.befit.dbschema.nutrientinformation.JsonEncoderDecoder._
import zio.http.Body
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import slick.jdbc.PostgresProfile.api._
import zio._
import zio.http._

class NutritionalInformationController(nis: NutritionalInformationRepository) {
  def createNewNutritionalInformation(
      body: Body
  ): ZIO[Database, Throwable, Response] = {
    body.asString
      .map(decode[NutrientInformation])
      .flatMap(
        _.fold(
          error => {
            ZIO.fail(new Exception(s"Error decoding, ${error.getMessage}"))
          },
          nutrientInformation => {
            nis.createNewNutritionalInformation(
              nutrientInformation.name,
              nutrientInformation.protein,
              nutrientInformation.fat,
              nutrientInformation.carbohydrate,
              nutrientInformation.unit,
              nutrientInformation.creator
            ) *> ZIO.succeed(
              Response.ok
            )
          }
        )
      )
  }

  def getNutritionalInformationById(
      id: Long
  ): ZIO[Database, Throwable, Response] = {
    nis.getNutritionalInformationById(id).flatMap { nutrientInformation =>
      ZIO.succeed(
        Response.json(
          nutrientInformation.asJson.toString()
        )
      )
    }
  }

  def deleteNutritionalInformation(
      id: Long,
      creator: Long
  ): ZIO[Database, Throwable, Response] = {
    nis
      .deleteNutritionalInformationByIdAndCreator(id = id, creator = creator)
      .flatMap { _ =>
        ZIO.succeed(
          Response.ok
        )
      }
  }

  def getNutritionalInformationByCreator(
      creator: Long
  ): ZIO[Database, Throwable, Response] = {
    nis.getNutritionalInformationByCreator(creator).flatMap {
      case Seq() =>
        ZIO.fail(
          new Exception(s"Nutritional Information with $creator not found")
        )
      case nutrientInformation =>
        ZIO.succeed(
          Response.json(
            nutrientInformation.asJson.toString()
          )
        )
    }
  }
}
