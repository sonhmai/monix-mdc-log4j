package com.scala.monixmdc.improved

import monix.eval.Task
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.{HttpApp, Uri}

// scalastyle:off magic.number
class ServerImproved {
  private val logger = org.log4s.getLogger
  import com.scala.monixmdc.common.Execution.tracingScheduler

  private var baseUri: Uri = _

  def httpApp(): HttpApp[Task] = {
    val wrappedRoutes = MiddlewareTask {
      RoutesTask.get
    }

    Router(
      "/" -> wrappedRoutes
    ).orNotFound
  }

  def start(): Unit = {

    val resource = BlazeServerBuilder[Task](tracingScheduler)
      .bindHttp(8082, "localhost")
      .withHttpApp(httpApp())
      .resource

    val (server, resourceReleaser) = resource.allocated.runSyncUnsafe()
    baseUri = server.baseUri

    logger.info(s"Http server started on port ${baseUri.port}")
    logger.info(
      s"monix.environment.localContextPropagation=${System.getProperty("monix.environment.localContextPropagation")}"
    )
  }
}
