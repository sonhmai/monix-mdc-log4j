package com.scala.monixmdc.old

import cats.effect.IO
import com.scala.monixmdc.common.MDCUtil
import io.circe.Json
import monix.eval.Task
import org.http4s.circe.jsonEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Request}

import scala.concurrent.Future

object RoutesIO {
  import com.scala.monixmdc.common.Execution.tracingScheduler
  private val logger = org.log4s.getLogger
  private val dsl = Http4sDsl[IO]
  import dsl._

  def get: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ _ -> _ => {
      for {
        _ <- Task.eval {
          logger.info("Calling another service to get data")
          Thread.sleep(200)
        }.to[IO]
        responseBody <- processRequest(req)
        resp <- Ok(responseBody)
      } yield resp
    }
  }

  private def processRequest(request: Request[IO]): IO[Json] = {
    val correlationId = MDCUtil.getCorrelationId[IO](request)
    val task = Task.fromFuture {
      Future {
        logger.info(s"Calling the database for correlationId $correlationId")
        Thread.sleep(1000) // simulating db call
        Json.fromString(correlationId)
      }
    }
    task.to[IO]
  }
}
