package com.raunakjodhawat.befit.dbschema.nutrientinformation

import io.circe._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

object JsonEncoderDecoder {
  implicit val nutrientInformationEncoder: Encoder[NutrientInformation] =
    deriveEncoder[NutrientInformation]
  implicit val nutrientInformationDecoder: Decoder[NutrientInformation] =
    deriveDecoder[NutrientInformation]
}
