package com.raunakjodhawat.befit.e2etest

import com.raunakjodhawat.befit.dbschema.nutrientinformation.NutrientInformation
import com.raunakjodhawat.befit.dbschema.nutrientinformation.JsonEncoderDecoder._
import zio.ZIO

import scala.util.{Failure, Success}
import io.circe.parser.decode
import net.domlom.websocket._
import net.domlom.websocket.model.Websocket

import scala.util.Try

class SearchSpec {
  var recordsReturned = 0
  val behavior: WebsocketBehavior = {
    WebsocketBehavior.empty
      .setOnMessage { (_, message) =>
        if (message.value != "Connected!") {
          decode[Seq[NutrientInformation]](message.value) match {
            case Right(value) =>
              recordsReturned = value.length
            case Left(err) =>
              println(err)
          }
        } else Seq[NutrientInformation]()
      }
      .setOnOpen(_ => ())
  }
  val socket: Websocket = Websocket(wsBasePath + "/search/ws", behavior)

  def runWSSearchFlow: ZIO[Any, Throwable, Unit] = ZIO.fromTry {
    for {
      _ <- Try(println("Running search flow with websocket"))
      _ <- socket.connect()
      _ <- socket.send("protein")
      _ = Thread.sleep(1000)
      _ <- socket.close()
      _ <-
        if (recordsReturned == 0)
          Failure(new Exception("No records returned"))
        else {
          println(s"Records returned: $recordsReturned")
          Success(recordsReturned)
        }
    } yield ()
  }
}
