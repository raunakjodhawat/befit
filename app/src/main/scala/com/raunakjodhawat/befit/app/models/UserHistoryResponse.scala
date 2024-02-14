package com.raunakjodhawat.befit.app.models

case class UserHistoryIncomingData(
    u_id: Long,
    ni_id: Long,
    quantity: Double
)

case class UserHistoryUpdateData(
    u_h_id: Long,
    u_id: Long,
    ni_id: Long,
    quantity: Double
)
case class UserHistoryResponseData(
    name: String,
    protein: Option[Double],
    carbohydrate: Option[Double],
    fat: Option[Double],
    unit: String
)
case class UserHistoryResponse(
    protein: Double,
    carbohydrate: Double,
    fat: Double,
    data: Seq[UserHistoryResponseData]
)
