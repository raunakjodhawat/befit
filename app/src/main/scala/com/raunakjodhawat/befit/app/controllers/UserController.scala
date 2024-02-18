package com.raunakjodhawat.befit.app.controllers

import com.raunakjodhawat.befit.app.repository.UserRepository
import com.raunakjodhawat.befit.dbschema.user.JsonEncoderDecoder._
import com.raunakjodhawat.befit.dbschema.user.User
import zio._
import zio.http._
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
            ur.createUser(user.id)
              .map(newUser => Response.json(newUser.asJson.toString()))
          }
        )
      )
  }

  def getUser(id: Long): ZIO[Database, Throwable, Response] =
    ur.getUserById(id)
      .fold(
        _ => Response.status(Status.NotFound),
        user => Response.json(user.asJson.toString())
      )

  def deleteUserById(id: Long): ZIO[Database, Throwable, Response] = {
    ur.deleteUser(id).map(_ => Response.ok)
  }
}
