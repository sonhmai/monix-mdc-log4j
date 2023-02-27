package com.scala.monixmdc.old

import cats.effect.{ContextShift, IO, Timer}
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.{HttpApp, Uri}

// scalastyle:off magic.number
class ServerOld {
  private val logger = org.log4s.getLogger
  import com.scala.monixmdc.common.Execution.tracingScheduler
  private implicit val timer: Timer[IO] = IO.timer(tracingScheduler)
  private implicit val contextShift: ContextShift[IO] =
    IO.contextShift(tracingScheduler)

  private var baseUri: Uri = _

  def httpApp(): HttpApp[IO] = {
    val wrappedRoutes = MiddlewareIO {
      RoutesIO.get
    }

    Router(
      "/" -> wrappedRoutes
    ).orNotFound
  }

  def start(): Unit = {

    val resource = BlazeServerBuilder[IO](tracingScheduler)
      .bindHttp(8081, "localhost")
      .withHttpApp(httpApp())
      .resource

    val (serverIO, _) = resource.allocated.unsafeRunSync()
    baseUri = serverIO.baseUri

    logger.info(s"Http server started on port ${baseUri.port}")
  }
}
