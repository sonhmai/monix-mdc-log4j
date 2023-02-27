package com.scala.monixmdc.improved

import cats.data.Kleisli
import com.scala.monixmdc.common.MDCUtil
import monix.eval.Task
import org.http4s.HttpRoutes
import org.log4s.MDC

object MiddlewareTask {
  private val logger = org.log4s.getLogger

  def apply(service: HttpRoutes[Task]): HttpRoutes[Task] = {
    Kleisli { req =>
      val correlationId = MDCUtil.getCorrelationId[Task](req)
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
