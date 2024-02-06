package com.raunakjodhawat.dbschema.models.nutrientinformation

case class NutrientInformation(
    name: String,
    protein: Option[Double],
    carbohydrate: Option[Double],
    fat: Option[Double]
)
