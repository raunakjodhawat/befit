package com.raunakjodhawat.befit.app.controllers

import com.raunakjodhawat.befit.app.repository.UserRepository
import com.raunakjodhawat.befit.dbschema.user.JsonEncoderDecoder._
import com.raunakjodhawat.befit.dbschema.user.User
import zio.ZIO
import zio.http.{Body, Response}
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
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
            for {
              newUser <- ur.createUser(user.id)
            } yield Response.json(User(newUser).asJson.toString())
          }
        )
      )
  }

  def getUserInfo(id: Long): ZIO[Database, Throwable, Response] = {
    for {
      user <- ur.getUserById(id)
    } yield Response.json(user.asJson.toString())
  }

  def deleteUserById(id: Long): ZIO[Database, Throwable, Response] = {
    for {
      deleted <- ur.deleteUser(id)
    } yield Response.json(s"Deleted $deleted user")
  }
}
