package com.raunakjodhawat.befit.e2etest

import com.raunakjodhawat.befit.dbschema.user.JsonEncoderDecoder._
import com.raunakjodhawat.befit.dbschema.user.User
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import zio._
import zio.http._

object UserSpec {
  val user: User = User(1)

  def createUser: ZIO[Client, Throwable, Long] = {
    for {
      client <- ZIO.service[Client]
      _ <- ZIO.succeed(println("Creating user"))
      createUserResponse <- client
        .url(createURL(basePath + "/user"))
        .post(
          "",
          body = createBody(user.asJson.toString())
        )
      _ <- ZIO.succeed(println("User creation successful"))
      userId <- createUserResponse.body.asString
        .map(decode[User])
        .flatMap(
          _.fold(error => ZIO.fail(error), user => ZIO.succeed(user.id))
        )
      _ <- ZIO.succeed(println("Successfully created user " + userId))
    } yield userId
  }

  def deleteUserById(id: Long): ZIO[Client, Throwable, Unit] = {
    for {
      client <- ZIO.service[Client]
      _ <- ZIO.succeed(println("Deleting user"))
      _ <- client
        .url(createURL(basePath + s"/user/$id"))
        .delete
        .mapError(error =>
          new Exception(s"Error deleting user, ${error.getMessage}")
        )
      _ <- ZIO.succeed(
        println(s"Able to delete user, with userId = $id")
      )
    } yield ()
  }

  def getUserById(id: Long): ZIO[Client, Throwable, Unit] = {
    for {
      client <- ZIO.service[Client]
      _ <- ZIO.succeed(println("Getting user"))
      _ <- client
        .url(createURL(basePath + s"/user/$id"))
        .get
        .mapError(error =>
          new Exception(s"Error getting user, ${error.getMessage}")
        )
      _ <- ZIO.succeed(
        println(s"Able to get user, with userId = $id")
      )
    } yield ()
  }
  def runUserFlow: ZIO[Client, Throwable, Unit] = {
    for {
      userId <- createUser
      _ <- getUserById(userId)
      _ <- deleteUserById(userId)
      _ <- getUserById(userId).fold(
        _ => ZIO.succeed(()),
        _ => ZIO.fail(new Exception("User not deleted"))
      )
    } yield ()
  }
}
