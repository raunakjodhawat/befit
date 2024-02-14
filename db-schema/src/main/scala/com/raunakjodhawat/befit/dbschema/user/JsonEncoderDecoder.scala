package com.raunakjodhawat.befit.dbschema.user

import io.circe._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import slick.ast.TypedType

import java.util.Date
import slick.jdbc.PostgresProfile.api._

import java.text.SimpleDateFormat

object JsonEncoderDecoder {
  val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  implicit val encodeDate: Encoder[Date] =
    Encoder.encodeString.contramap[Date](_.getTime.toString)
  implicit val decodeDate: Decoder[Date] =
    Decoder.decodeString.map(s => new Date(s.toLong))

  implicit val userEncoder: Encoder[User] =
    deriveEncoder[User]
  implicit val userDecoder: Decoder[User] =
    deriveDecoder[User]

  implicit val userHistoryEncoder: Encoder[UserHistory] =
    deriveEncoder[UserHistory]
  implicit val userHistoryDecoder: Decoder[UserHistory] =
    deriveDecoder[UserHistory]

  implicit val dateMapping: TypedType[Date] =
    MappedColumnType.base[Date, String](
      date => sdf.format(date),
      dateInString => sdf.parse(dateInString)
    )
}
