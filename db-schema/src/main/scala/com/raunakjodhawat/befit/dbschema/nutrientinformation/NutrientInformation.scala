package com.raunakjodhawat.befit.dbschema.nutrientinformation

case class NutrientInformation(
    id: Long,
    name: String,
    protein: Option[Double],
    carbohydrate: Option[Double],
    fat: Option[Double],
    unit: String,
    creator: Long
)
