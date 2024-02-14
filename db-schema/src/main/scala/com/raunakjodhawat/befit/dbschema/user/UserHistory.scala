package com.raunakjodhawat.befit.dbschema.user

case class UserHistory(
    id: Long,
    u_id: Long,
    ni_id: Long,
    qty: Double,
    updated_at: String
)
