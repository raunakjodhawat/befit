package com.raunakjodhawat.befit

import io.circe.parser.parse
import zio.http.{Body, URL}

package object e2etest {
  val basePath = "http://localhost:8080/api/v1"
  def createURL(url: String): URL = URL.decode(url).toOption.get

  def createBody(body: String): Body =
    Body.fromString(parse(body).toOption.get.toString())
}
