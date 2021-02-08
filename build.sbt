name := "ScalaDragons"


lazy val versions = new {
  val scala = "2.13.3"
  val scalatest = "3.2.2"
  val mockito = "1.10.19"
}

version := "0.1"

scalaVersion := versions.scala

libraryDependencies += "org.mockito" % "mockito-all" % versions.mockito % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % versions.scalatest % "test"
