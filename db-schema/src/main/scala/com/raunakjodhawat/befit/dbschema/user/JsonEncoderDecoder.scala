package com.raunakjodhawat.befit.dbschema.user

import io.circe._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import slick.ast.TypedType

import slick.jdbc.PostgresProfile.api._

object JsonEncoderDecoder {

  implicit val userEncoder: Encoder[User] =
    deriveEncoder[User]
  implicit val userDecoder: Decoder[User] =
    deriveDecoder[User]

  implicit val userHistoryEncoder: Encoder[UserHistory] =
    deriveEncoder[UserHistory]
  implicit val userHistoryDecoder: Decoder[UserHistory] =
    deriveDecoder[UserHistory]

  implicit val incomingCreateUserEncoder: Encoder[IncomingCreateUser] =
    deriveEncoder[IncomingCreateUser]
  implicit val incomingCreateUserDecoder: Decoder[IncomingCreateUser] =
    deriveDecoder[IncomingCreateUser]

  implicit val outgoingCreateUserEncoder: Encoder[OutgoingCreateUser] =
    deriveEncoder[OutgoingCreateUser]
  implicit val outgoingCreateUserDecoder: Decoder[OutgoingCreateUser] =
    deriveDecoder[OutgoingCreateUser]

}
