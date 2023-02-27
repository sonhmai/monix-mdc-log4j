package com.scala.monixmdc
import com.scala.monixmdc.old.ServerOld

object AppOld {
  def main(args: Array[String]): Unit = {
    val server = new ServerOld()
    server.start()
  }
}
