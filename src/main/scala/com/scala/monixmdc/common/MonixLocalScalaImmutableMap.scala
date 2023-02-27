package com.scala.monixmdc.common

import monix.execution.misc.Local
import org.apache.logging.log4j.spi.ThreadContextMap

import scala.jdk.CollectionConverters.MapHasAsJava

/**
 *
 */
class MonixLocalScalaImmutableMap extends ThreadContextMap {
  private val logger = org.log4s.getLogger
  private[this] val local = Local[Map[String, String]](Map.empty[String, String])

  override def put(key: String, value: String): Unit = {
    local.update(local() + (key -> value))
  }

  override def get(key: String): String = local().get(key).orNull

  override def remove(key: String): Unit = local.update(local() - key)

  override def clear(): Unit = local.clear()

  override def containsKey(key: String): Boolean = local().contains(key)

  override def getCopy: java.util.Map[String, String] = local().asJava

  override def getImmutableMapOrNull: java.util.Map[String, String] = {
    if (local().isEmpty) {
      null
    } else {
      getCopy
    }
  }

  override def isEmpty: Boolean = local().isEmpty
}
