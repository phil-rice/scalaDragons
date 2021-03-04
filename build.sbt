name := "ScalaDragons"


lazy val versions = new {
  val scala = "2.13.3"
  val scalatest = "3.2.2"
  val scalatestplus = "3.2.2.0"
  val log4j = "2.14.0"
}

version := "0.1"

scalaVersion := versions.scala

libraryDependencies += "org.scalatestplus" %% "mockito-3-4" % "3.2.2.0" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % versions.scalatest % "test"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % versions.log4j
