package com.raunakjodhawat.befit.e2etest

import com.raunakjodhawat.befit.dbschema.user.User
import com.raunakjodhawat.befit.dbschema.user.JsonEncoderDecoder._
import zio._
import zio.http._
import io.circe.parser.decode
import io.circe.syntax.EncoderOps

object UserSpec {
  val user: User = User(1)
  def runUserFlow(client: Client): ZIO[Client, Throwable, Unit] = {
    for {
      client <- ZIO.service[Client]
      _ <- ZIO.succeed(println("Running user flow"))
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
      _ <- client
        .url(createURL(basePath + s"/user/$userId"))
        .get
        .mapError(error =>
          new Exception(s"Error getting user, ${error.getMessage}")
        )
      _ <- ZIO.succeed(println("Able to query recently created user"))
      _ <- client
        .url(createURL(basePath + s"/user/$userId"))
        .delete
        .mapError(error =>
          new Exception(s"Error deleting user, ${error.getMessage}")
        )
      _ <- ZIO.succeed(
        println(s"Able to delete recently created user, with userId = $userId")
      )
      _ <- client
        .url(createURL(basePath + s"/user/$userId"))
        .get
        .fold(
          error => ZIO.fail(error),
          response =>
            if (response.status.code == 404) ZIO.succeed(response)
            else ZIO.fail(new Exception("User not deleted"))
        )
      _ <- ZIO.succeed(println("User deleted successfully"))
    } yield ()
  }
}
