import sbt._

object Dependencies {

  private val catsVersion = "0.8.1"

  private val catsCore = "org.typelevel" %% "cats-core" % catsVersion
  private val catsFree = "org.typelevel" %% "cats-free" % catsVersion

  private val scalikeJdbc = "org.scalikejdbc" %% "scalikejdbc" % "2.5.0"

  private val h2Driver = "com.h2database" % "h2" % "1.4.193"

  private val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.0.0"
  private val akkaTest = "com.typesafe.akka" %% "akka-testkit" % "2.4.14"

  private val diffson = "org.gnieh" %% "diffson-play-json" % "2.1.1"

  private val slf4j = "org.slf4j" % "slf4j-api" % "1.7.21"

  private val logback = "ch.qos.logback" % "logback-classic" % "1.1.7"

  private val specs2 = "org.specs2" %% "specs2-core" % "3.8.6"

  val core = Seq(
    catsCore % Compile,
    catsFree % Compile,
    scalikeJdbc % Compile,
    akkaHttp % Compile,
    diffson % Compile,

    slf4j % "it",
    logback % "it",
    specs2  % "test;it",
    akkaTest % "test;it",
    h2Driver % IntegrationTest
  )

  val play = Seq(specs2 % Test)

}
