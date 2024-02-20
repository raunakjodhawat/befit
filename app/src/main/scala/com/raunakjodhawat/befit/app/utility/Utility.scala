package com.raunakjodhawat.befit.app.utility

object Utility {
  def getSalt: String = {
    val random = new scala.util.Random
    random.alphanumeric.take(10).mkString
  }

  def getHashedPassword(password: String, salt: String): String = {
    val md = java.security.MessageDigest.getInstance("SHA-256")
    val text = password + salt
    md.digest(text.getBytes("UTF-8")).map("%02x".format(_)).mkString
  }

  def verifySaltedPassword(
      password: String,
      salt: String,
      hashedPassword: String
  ): Boolean = {
    val md = java.security.MessageDigest.getInstance("SHA-256")
    val text = password + salt
    val hashed =
      md.digest(text.getBytes("UTF-8")).map("%02x".format(_)).mkString
    hashed == hashedPassword
  }
}
