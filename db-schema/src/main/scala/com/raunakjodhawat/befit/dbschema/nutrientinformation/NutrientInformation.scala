package com.raunakjodhawat.befit.dbschema.nutrientinformation

case class NutrientInformation(
    name: String,
    protein: Option[Double],
    carbohydrate: Option[Double],
    fat: Option[Double]
)
