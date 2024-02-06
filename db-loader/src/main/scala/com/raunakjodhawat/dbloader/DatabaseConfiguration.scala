package com.raunakjodhawat.dbloader

object DatabaseConfiguration extends App {
//  println("Starting database configuration")
//  val db: ZIO[Any, Throwable, Database] = dbSetup.initialize

  private val mapping: Map[String, String] = Map(
    "Protein" -> "protein",
    "Carbohydrate, by difference" -> "carbohydrate",
    "Total lipid (fat)" -> "fat"
  )

  println(
    RawFileReader.readFile(
      "/Users/raunakjodhawat/code/befit/db-loader/src/main/resources/raw.json",
      mapping
    )
  )

}
