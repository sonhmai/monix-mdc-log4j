package com.scala.monixmdc.old

import cats.data.Kleisli
import cats.effect.IO
import com.scala.monixmdc.common.MDCUtil
import org.http4s.HttpRoutes
import org.log4s.MDC

object MiddlewareIO {
    private val logger = org.log4s.getLogger

    def apply(service: HttpRoutes[IO]): HttpRoutes[IO] = {
      Kleisli { req =>
        val correlationId = MDCUtil.getCorrelationId[IO](req)
        logger.info(s"putting mdc, corrId ${correlationId}")
        MDCUtil.putCorrelationId(correlationId)
        service(req).map { response =>
          MDC.clear()
          logger.info(s"cleared mdc for request corrId ${correlationId}")
          response
        }
      }
    }
}
