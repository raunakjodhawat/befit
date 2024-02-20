package com.raunakjodhawat.befit.dbschema.user

case class User(
    id: Long,
    username: String,
    salt: String,
    hashedPassword: String
)

case class IncomingCreateUser(
    username: String,
    password: String
)

case class OutgoingCreateUser(
    id: Long
)
