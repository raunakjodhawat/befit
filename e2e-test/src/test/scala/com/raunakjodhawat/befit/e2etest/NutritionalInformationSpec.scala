package com.raunakjodhawat.befit.e2etest
import com.raunakjodhawat.befit.dbschema.nutrientinformation.NutrientInformation
import com.raunakjodhawat.befit.dbschema.nutrientinformation.JsonEncoderDecoder._
import io.circe.syntax.EncoderOps
import io.circe.parser.decode
import zio.ZIO
import zio.http.Client

object NutritionalInformationSpec {
  def getNutritionalInformation(c_id: Long): NutrientInformation =
    NutrientInformation(
      id = 1L,
      name = "some-100",
      protein = Some(100),
      carbohydrate = Some(100),
      fat = Some(100),
      unit = "g",
      creator = c_id
    )

  def createNutritionalInformation(
      c_id: Long
  ): ZIO[Client, Throwable, NutrientInformation] = {
    val ni = getNutritionalInformation(c_id)
    for {
      client <- ZIO.service[Client]
      _ <- ZIO.succeed(println("Creating Nutritional Information"))
      createNutritionalInformationResponse <- client
        .url(createURL(basePath + "/create/food"))
        .post(
          "",
          body = createBody(ni.asJson.toString())
        )
      _ <- ZIO.succeed(println("Nutritional Information creation successful"))
      ni_response <- createNutritionalInformationResponse.body.asString
        .map(decode[NutrientInformation])
        .flatMap(
          _.fold(
            error => ZIO.fail(error),
            ni => {
              println("Successfully created Nutritional Information " + ni.id)
              ZIO.succeed(ni)
            }
          )
        )
    } yield ni_response
  }

  def deleteNutritionalInformation(
      n_id: Long,
      c_id: Long
  ): ZIO[Client, Throwable, Unit] =
    for {
      client <- ZIO.service[Client]
      _ <- ZIO.succeed(println("Deleting Nutritional Information"))
      _ <- client
        .url(createURL(basePath + s"/food/$n_id/$c_id"))
        .delete
        .mapError(error =>
          new Exception(
            s"Error deleting Nutritional Information, ${error.getMessage}"
          )
        )
      _ <- ZIO.succeed(println("Nutritional Information deletion successful"))
    } yield ()

  def getNutritionalInformationByCreatorId(
      c_id: Long
  ): ZIO[Client, Throwable, Option[NutrientInformation]] =
    for {
      client <- ZIO.service[Client]
      _ <- ZIO.succeed(println("Getting Nutritional Information"))
      getNutritionalInformationResponse <- client
        .url(createURL(basePath + s"/user/food/$c_id"))
        .get
        .mapError(error =>
          new Exception(
            s"Error getting Nutritional Information, ${error.getMessage}"
          )
        )
      _ <- ZIO.succeed(println("Nutritional Information get successful"))
      ni_response <- getNutritionalInformationResponse.body.asString
        .map(decode[NutrientInformation])
        .flatMap(
          _.fold(
            _ => ZIO.succeed(None),
            ni => {
              println("Successfully got Nutritional Information " + ni.id)
              ZIO.succeed(Some(ni))
            }
          )
        )
    } yield ni_response

}
