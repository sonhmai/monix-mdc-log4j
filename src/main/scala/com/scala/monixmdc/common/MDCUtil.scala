package com.scala.monixmdc.common
import org.http4s.Request
import org.http4s.util.CaseInsensitiveString
import org.log4s.MDC

object MDCUtil {
  val CorrelationIdKey = "correlationId"

  def getCorrelationId[F[_]](request: Request[F]): String = {
    request.headers
      .get(CaseInsensitiveString(CorrelationIdKey))
      .map(_.value)
      .getOrElse("")
  }

  def putCorrelationId(correlationId: String): Unit =
    MDC.put(key = CorrelationIdKey, value = correlationId)
}
