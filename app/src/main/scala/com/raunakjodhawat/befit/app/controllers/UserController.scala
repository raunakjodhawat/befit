package com.raunakjodhawat.befit.app.controllers

import com.raunakjodhawat.befit.app.repository.UserRepository
import com.raunakjodhawat.befit.dbschema.user.JsonEncoderDecoder._
import com.raunakjodhawat.befit.dbschema.user.User
import zio.ZIO
import zio.http.{Body, Response}
import io.circe.parser.decode
import slick.jdbc.PostgresProfile.api._

class UserController(ur: UserRepository) {
  def createUser(
      body: Body
  ): ZIO[Database, Throwable, Response] = {
    body.asString
      .map(decode[User])
      .flatMap(
        _.fold(
          error => {
            ZIO.fail(new Exception(s"Error decoding, ${error.getMessage}"))
          },
          user => {
            ur.createUser(
              user.id
            ) *> ZIO.succeed(
              Response.ok
            )
          }
        )
      )
  }
}
