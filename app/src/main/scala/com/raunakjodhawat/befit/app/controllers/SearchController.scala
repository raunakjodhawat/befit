package com.raunakjodhawat.befit.app.controllers

import com.raunakjodhawat.befit.app.repository.{
  NutritionalInformationRepository,
  SearchRepository
}
import com.raunakjodhawat.befit.dbschema.nutrientinformation.JsonEncoderDecoder._
import slick.jdbc.PostgresProfile.api._
import zio._
import zio.http.ChannelEvent.{
  ExceptionCaught,
  Read,
  UserEvent,
  UserEventTriggered
}
import zio.http._
import io.circe.syntax._

import scala.util.{Success, Try}
import slick.jdbc.PostgresProfile.api._
import zio._
import zio.http._
class SearchController(
    sr: SearchRepository,
    nir: NutritionalInformationRepository,
    basePath: Path
) {
  val searchRouter: Http[Database, Throwable, Request, Response] = Http
    .collectZIO[Request] {
      case Method.GET -> basePath / "search" / "ws" / string(prefix) =>
        searchByPrefix(prefix).map(Response.json(_))
      case Method.GET -> basePath / "search" / "id" / long(id) =>
        searchById(id)
    }
  val socketApp: Handler[Database, Throwable, WebSocketChannel, Nothing] =
    Handler.webSocket { channel =>
      channel.receiveAll {
        case Read(WebSocketFrame.Text(text)) => {
          searchByPrefix(text).flatMap(response =>
            channel.send(Read(WebSocketFrame.text(response)))
          )
        }

        // Send a "greeting" message to the server once the connection is established
        case UserEventTriggered(UserEvent.HandshakeComplete) =>
          channel.send(Read(WebSocketFrame.text("Connected!")))

        // Log when the channel is getting closed
        case Read(WebSocketFrame.Close(status, reason)) =>
          Console.printLine(
            "Closing channel with status: " + status + " and reason: " + reason
          )

        // Print the exception if it's not a normal close
        case ExceptionCaught(cause) =>
          Console.printLine(s"Channel error!: ${cause.getMessage}")

        case _ =>
          ZIO.unit
      }
    }
  private def searchByPrefix(text: String): ZIO[Database, Throwable, String] = {
    sr.searchByPrefix(text).map(_.asJson.toString())
  }

  def searchById(text: Long): ZIO[Database, Throwable, Response] = {
    for {
      u <- nir.getNutritionalInformationById(text)
    } yield Response.json(u.asJson.toString())
  }
}
