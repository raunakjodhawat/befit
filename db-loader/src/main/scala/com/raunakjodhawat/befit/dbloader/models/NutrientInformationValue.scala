package com.raunakjodhawat.befit.dbloader.models

case class NutrientInformationValue(
    protein: Option[Double] = None,
    carbohydrate: Option[Double] = None,
    fat: Option[Double] = None
)
