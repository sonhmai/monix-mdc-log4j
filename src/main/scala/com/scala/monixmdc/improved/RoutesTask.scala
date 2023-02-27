package com.scala.monixmdc.improved
import com.scala.monixmdc.common.MDCUtil
import io.circe.Json
import monix.eval.Task
import org.http4s.circe.jsonEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Request}

import scala.concurrent.Future

object RoutesTask {
  private val logger = org.log4s.getLogger
  private val dsl = Http4sDsl[Task]
  import com.scala.monixmdc.common.Execution.tracingScheduler
  import dsl._

  def get: HttpRoutes[Task] = HttpRoutes.of[Task] {
    case req @ _ -> _ => {
      for {
        _ <- Task.eval {
          logger.info(s"Calling another service to get data for correlationId ${MDCUtil.getCorrelationId(req)}")
          Thread.sleep(200)
        }
        responseBody <- processRequest(req)
        resp <- Ok(responseBody)
      } yield resp
    }
  }

  private def processRequest(request: Request[Task]): Task[Json] = {
    val correlationId = MDCUtil.getCorrelationId[Task](request)
    val task = Task.fromFuture {
      for {
        _ <- Future(MDCUtil.putCorrelationId(correlationId))
        result <- Future {
          logger.info(s"Calling the database for correlationId $correlationId")
          Thread.sleep(1000) // simulating db call
          Json.fromString(correlationId)
        }
      } yield result
    }
    task
  }
}
