package com.raunakjodhawat.dbloader

import io.circe.parser.decode
import io.circe.generic.auto._ // For automatic derivation
import scala.util.Using
import java.nio.file.Paths

case class SurveyFood(
    description: String,
    foodNutrients: Seq[FoodNutrient]
)

case class FoodNutrient(
    nutrient: Nutrient,
    amount: Double
)

case class Nutrient(
    name: String,
    unitName: String
)

case class RawData(SurveyFoods: Seq[SurveyFood])

object Application {
  def main(args: Array[String]): Unit = {
    val parser = new io.circe.jawn.JawnParser()

    Using(
      scala.io.Source.fromFile(
        Paths
          .get(
            "/Users/raunakjodhawat/code/befit/db-loader/src/main/resources/raw.json"
          )
          .toFile
      )
    ) { source =>
      val jsonString = source.mkString
      parser.parse(jsonString) match {
        case Right(json) =>
          decode[RawData](json.toString()) match {
            case Right(rawData) =>
              rawData.SurveyFoods.foreach { surveyFood =>
                // Process each SurveyFood object individually
                surveyFood.foodNutrients.foreach { nutrient =>
                  println(s"Food description: ${surveyFood.description}")
                  println(s"name: ${nutrient.nutrient.name}")
                  println(s"unit: ${nutrient.nutrient.unitName}")
                  println(s"amount: ${nutrient.amount}")
                // Perform additional processing here
                }
              }
            case Left(error) =>
              println(s"Error decoding JSON: $error")
          }
        case Left(error) =>
          println(s"Error parsing JSON: $error")
      }
    }
  }
}
