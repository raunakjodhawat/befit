package com.raunakjodhawat.befit.dbschema

case class UserHistoryResponseData(
                                    name: String,
                                    protein: Option[Double],
                                    carbohydrate: Option[Double],
                                    fat: Option[Double],
                                    quantity: Double,
                                    unit: String
                                  )