package com.scala.monixmdc

import com.scala.monixmdc.improved.ServerImproved

object AppImproved {
  def main(args: Array[String]): Unit = {
    System.setProperty("monix.environment.localContextPropagation", "1")
    val server = new ServerImproved()
    server.start()
  }
}
