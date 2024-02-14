package com.raunakjodhawat.befit.dbschema.user

import java.util.Date

case class UserHistory(
    id: Long,
    u_id: Long,
    ni_id: Long,
    qty: Double,
    created_at: Option[Date],
    updated_at: Option[Date]
)
