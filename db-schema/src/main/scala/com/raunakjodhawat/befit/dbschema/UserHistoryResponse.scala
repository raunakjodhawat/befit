package com.raunakjodhawat.befit.dbschema

case class UserHistoryResponse(
    protein: Double,
    carbohydrate: Double,
    fat: Double,
    data: Seq[UserHistoryResponseData]
)
