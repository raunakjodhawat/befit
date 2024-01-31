package com.raunakjodhawat.dbloader

import io.circe.parser.decode
import io.circe.jawn.JawnParser
import io.circe.generic.auto._

import scala.util.Using
import java.nio.file.Paths
import scala.io.Source.fromFile

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
  val importantMetrics: Set[String] =
    Set[String]("Protein", "Carbohydrate, by difference", "Total lipid (fat)")
  def main(args: Array[String]): Unit = {
    val parser = new JawnParser()
    Using(
      fromFile(
        Paths
          .get(
            "./src/main/resources/raw.json"
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
                println(s"Description: ${surveyFood.description}")
                val foodNutrient = surveyFood.foodNutrients.filter(nutrient =>
                  importantMetrics.contains(nutrient.nutrient.name)
                )
                println(foodNutrient)
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
