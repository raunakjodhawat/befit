package com.raunakjodhawat.befit.app.middleware

import com.raunakjodhawat.befit.app.repository.UserRepository
import com.raunakjodhawat.befit.app.utility.Utility
import zio.ZIO
import zio.http.{Headers, HttpError, Response}

import java.util.Base64
import slick.jdbc.PostgresProfile.api._

class AuthMiddleware(ur: UserRepository) {
  private val BearerPrefix = "Bearer "

  def authenticator(headers: Headers): ZIO[Database, Throwable, Response] = {
    headers.get("Authorization") match {
      case Some(header) if header.startsWith(BearerPrefix) =>
        val token = header.drop(BearerPrefix.length)
        decodeToken(token)
      case _ =>
        ZIO.fail(new Exception("Authorization header not found"))
    }
  }

  private def decodeToken(token: String): ZIO[Database, Throwable, Response] = {
    try {
      val decodedBytes = Base64.getDecoder.decode(token)
      val decodedString = new String(decodedBytes)
      val splitDecodedString = decodedString.split(":")

      if (splitDecodedString.length != 2) {
        ZIO.fail(new Exception("Invalid token format"))
      } else {
        val username = splitDecodedString(0)
        val password = splitDecodedString(1)
        for {
          user <- ur.getByUsername(username)
          newHashedPassword = Utility.getHashedPassword(password, user.salt)
          response <-
            if (newHashedPassword == user.hashedPassword)
              ZIO.succeed(Response.ok)
            else
              ZIO.fail(HttpError.Unauthorized("Invalid username or password"))
        } yield response
      }
    } catch {
      case _: IllegalArgumentException =>
        ZIO.fail(new Exception("Invalid Base64 token"))
    }
  }
}
