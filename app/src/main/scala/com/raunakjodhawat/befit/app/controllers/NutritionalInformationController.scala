package com.raunakjodhawat.befit.app.controllers

import com.raunakjodhawat.befit.app.repository.NutritionalInformationRepository
import com.raunakjodhawat.befit.dbschema.nutrientinformation.NutrientInformation
import com.raunakjodhawat.befit.dbschema.nutrientinformation.JsonEncoderDecoder._
import zio.http.Body

import io.circe.parser.decode
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
              nutrientInformation.carbohydrate
            ) *> ZIO.succeed(
              Response.ok
            )
          }
        )
      )
  }
}
