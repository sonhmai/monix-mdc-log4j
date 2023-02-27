import sbt._

object Dependencies {

  lazy val LogbackVersion = "1.2.3"
  lazy val log4jCoreVersion = "2.16.0"
  lazy val log4sVersion = "1.10.0"
  lazy val monixVersion = "3.3.0"

  val monixMDCDeps: Seq[ModuleID] = Seq(
    "org.apache.logging.log4j" % "log4j-core" % log4jCoreVersion,
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jCoreVersion,
    "org.log4s" %% "log4s" % log4sVersion,
    "io.monix" %% "monix" % monixVersion,
  )
}
