ThisBuild / scalaVersion := "2.13.6"

val root = (project in file("."))
  .settings(name := "monix-mdc-log4j")
  .settings(libraryDependencies ++= Dependencies.monixMDCDeps)