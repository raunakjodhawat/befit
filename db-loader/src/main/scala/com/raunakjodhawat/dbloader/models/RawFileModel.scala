package com.raunakjodhawat.dbloader.models

object RawFileModel {
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
}
