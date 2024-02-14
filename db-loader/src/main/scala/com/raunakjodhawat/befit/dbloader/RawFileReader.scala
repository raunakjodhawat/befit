package com.raunakjodhawat.befit.dbloader

import com.raunakjodhawat.befit.dbloader.models.NutrientInformationValue
import com.raunakjodhawat.befit.dbloader.models.RawFileModel.RawData
import io.circe.jawn.JawnParser
import io.circe.generic.auto._
import io.circe.parser.decode

import scala.util.Using
import scala.io.Source.fromFile
import java.nio.file.Paths

object RawFileReader {
  def readFile(
      fileName: String,
      nameMappings: Map[String, String]
  ): Map[String, NutrientInformationValue] = {
    Using(fromFile(Paths.get(fileName).toFile)) { source =>
      val parser = new JawnParser()
      parser.parse(source.mkString) match {
        case Right(json) =>
          decode[RawData](json.toString()) match {
            case Right(rawData) =>
              rawData.SurveyFoods.map { surveyFood =>
                val nutrientInformation: Seq[(String, String, Double)] =
                  surveyFood.foodNutrients
                    .filter(nutrient =>
                      nameMappings.contains(nutrient.nutrient.name)
                    )
                    .map(x =>
                      (
                        nameMappings(x.nutrient.name),
                        x.nutrient.unitName,
                        x.amount
                      )
                    )

                val nutrientInfoValue = NutrientInformationValue(
                  nutrientInformation.find(_._1 == "protein").map(_._3),
                  nutrientInformation.find(_._1 == "carbohydrate").map(_._3),
                  nutrientInformation.find(_._1 == "fat").map(_._3),
                  nutrientInformation.head._2
                )

                (surveyFood.description, nutrientInfoValue)
              }.toMap
            case Left(error) =>
              println(s"Error decoding JSON: $error")
              Map.empty[String, NutrientInformationValue]
          }
        case Left(error) =>
          println(s"Error parsing JSON: $error")
          Map.empty[String, NutrientInformationValue]
      }
    }.getOrElse {
      println("Failed to read file")
      Map.empty[String, NutrientInformationValue]
    }
  }
}
