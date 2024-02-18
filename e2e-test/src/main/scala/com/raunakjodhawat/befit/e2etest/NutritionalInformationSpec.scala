package com.raunakjodhawat.befit.e2etest
import com.raunakjodhawat.befit.dbschema.nutrientinformation.JsonEncoderDecoder._
import com.raunakjodhawat.befit.dbschema.nutrientinformation.NutrientInformation
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import zio.ZIO
import zio.http.Client

object NutritionalInformationSpec {
  def getNutritionalInformation(c_id: Long): NutrientInformation =
    NutrientInformation(
      id = 1L,
      name = "protein is life",
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
        .url(createURL(basePath + "/ni"))
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
        .url(createURL(basePath + s"/ni/$n_id/creator/$c_id"))
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
        .url(createURL(basePath + s"/ni/creator/$c_id"))
        .get
        .mapError(error =>
          new Exception(
            s"Error getting Nutritional Information, ${error.getMessage}"
          )
        )
      _ <- ZIO.succeed(println("Nutritional Information get successful"))
      ni_response <- getNutritionalInformationResponse.body.asString
        .map(decode[Seq[NutrientInformation]])
        .flatMap(
          _.fold(
            _ => ZIO.fail(new Exception("Nutritional Information not found")),
            ni => {
              println("Successfully got Nutritional Information " + ni.head.id)
              ZIO.succeed(Some(ni.head))
            }
          )
        )
    } yield ni_response

  def getNutritionalInformationById(
      n_id: Long
  ): ZIO[Client, Throwable, Option[NutrientInformation]] =
    for {
      client <- ZIO.service[Client]
      _ <- ZIO.succeed(
        println("Getting Nutritional Information")
      )
      getNutritionalInformationResponse <- client
        .url(createURL(basePath + s"/ni/$n_id"))
        .get
        .mapError(error =>
          new Exception(
            s"Error getting Nutritional Information, ${error.getMessage}"
          )
        )
      _ <- ZIO.succeed(
        println("Nutritional Information get successful")
      )
      ni_response <- getNutritionalInformationResponse.body.asString
        .map(decode[NutrientInformation])
        .flatMap(
          _.fold(
            _ => ZIO.fail(new Exception("Nutritional Information not found")),
            ni => {
              println("Successfully got Nutritional Information " + ni.id)
              ZIO.succeed(Some(ni))
            }
          )
        )
    } yield ni_response

  def updateNutritionalInformation(
      ni: NutrientInformation
  ): ZIO[Client, Throwable, NutrientInformation] = {
    for {
      client <- ZIO.service[Client]
      _ <- ZIO.succeed(println("Updating Nutritional Information"))
      updateNutritionalInformationResponse <- client
        .url(createURL(basePath + s"/ni"))
        .put(
          "",
          body = createBody(ni.asJson.toString())
        )
      _ <- ZIO.succeed(println("Nutritional Information update successful"))
      ni_response <- updateNutritionalInformationResponse.body.asString
        .map(decode[NutrientInformation])
        .flatMap(
          _.fold(
            error => ZIO.fail(error),
            ni => {
              println("Successfully updated Nutritional Information " + ni.id)
              ZIO.succeed(ni)
            }
          )
        )
    } yield ni_response
  }
  def runNutritionalInformationFlow(
      c_id: Long
  ): ZIO[Client, Throwable, Unit] = {
    for {
      ni <- createNutritionalInformation(c_id)
      _ <- getNutritionalInformationByCreatorId(c_id)
      _ <- getNutritionalInformationById(ni.id)
      updated_ni = ni.copy(name = "some-200", protein = Some(200))
      _ <- updateNutritionalInformation(updated_ni)
      update_ni_from_db <- getNutritionalInformationById(ni.id)
      _ <- update_ni_from_db match {
        case Some(ni) =>
          if (ni.name == "some-200" && ni.protein.contains(200)) ZIO.succeed()
          else ZIO.fail(new Exception("Nutritional Information not updated"))
        case None =>
          ZIO.fail(new Exception("Nutritional Information not found"))
      }
      _ <- deleteNutritionalInformation(ni.id, c_id)
      response <- getNutritionalInformationById(ni.id)
        .fold(
          _ => ZIO.succeed(),
          _ => ZIO.fail(new Exception("Nutritional Information not deleted"))
        )
    } yield response

  }

}
