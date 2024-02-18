package com.raunakjodhawat.befit.dbschema

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
object JsonEncoderDecoder {

  implicit val userHistoryResponseDataEncoder
      : Encoder[UserHistoryResponseData] =
    deriveEncoder[UserHistoryResponseData]
  implicit val userHistoryResponseDataDecoder
      : Decoder[UserHistoryResponseData] =
    deriveDecoder[UserHistoryResponseData]

  implicit val userHistoryResponseEncoder: Encoder[UserHistoryResponse] =
    deriveEncoder[UserHistoryResponse]
  implicit val userHistoryResponseDecoder: Decoder[UserHistoryResponse] =
    deriveDecoder[UserHistoryResponse]

  implicit val userHistoryIncomingDataEncoder
      : Encoder[UserHistoryIncomingData] =
    deriveEncoder[UserHistoryIncomingData]
  implicit val userHistoryIncomingDataDecoder
      : Decoder[UserHistoryIncomingData] =
    deriveDecoder[UserHistoryIncomingData]

  implicit val userHistoryUpdateDataEncoder: Encoder[UserHistoryUpdateData] =
    deriveEncoder[UserHistoryUpdateData]
  implicit val userHistoryUpdateDataDecoder: Decoder[UserHistoryUpdateData] =
    deriveDecoder[UserHistoryUpdateData]

}
