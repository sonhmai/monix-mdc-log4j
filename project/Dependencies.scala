import sbt._

object Dependencies {

  val LogbackVersion = "1.2.3"
  val log4jCoreVersion = "2.16.0"
  val log4sVersion = "1.10.0"
  val monixVersion = "3.3.0"
  val Http4sVersion = "0.21.16"

  val monixMDCDeps: Seq[ModuleID] = Seq(
    "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
    "org.http4s" %% "http4s-circe" % Http4sVersion,
    "org.http4s" %% "http4s-dsl" % Http4sVersion,
    "org.apache.logging.log4j" % "log4j-core" % log4jCoreVersion,
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jCoreVersion,
    "org.log4s" %% "log4s" % log4sVersion,
    "io.monix" %% "monix" % monixVersion,
  )
}
