package com.scala.monixmdc.common
import monix.execution.Scheduler
import monix.execution.schedulers.TracingScheduler

object Execution {
  implicit val tracingScheduler: Scheduler = TracingScheduler(
    Scheduler.io(
      name = "IO",
      daemonic = false
    )
  )

}
