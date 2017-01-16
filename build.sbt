import sbt.Keys._

val commonSettings = Seq(
  organization := "com.github.pheymann",
  scalaVersion := "2.11.8",
  version      := "0.3.1-RC",

  sonatypeProfileName := "pheymann",
  pomExtra in Global := {
    <url>https://github.com/pheymann/rest-refactoring-test</url>
      <licenses>
        <license>
          <name>MIT</name>
          <url>https://github.com/pheymann/rest-refactoring-test/blob/develop/LICENSE</url>
        </license>
      </licenses>
      <scm>
        <connection>scm:git:github.com/pheymann/rest-refactoring-test</connection>
        <developerConnection>scm:git:git@github.com:pheymann/rest-refactoring-test</developerConnection>
        <url>github.com/pheymann/rest-refactoring-test</url>
      </scm>
      <developers>
        <developer>
          <id>pheymann</id>
          <name>Paul Heymann</name>
          <url>https://github.com/pheymann</url>
        </developer>
      </developers>
  }
)

lazy val `rest-refactoring-test-tool` = project.in(file("."))
  .settings(commonSettings)
  .aggregate(core, play)

lazy val core = project.in(file("core"))
  .settings(commonSettings)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(
    name := "rrt-core",
    libraryDependencies ++= Dependencies.core,
    parallelExecution in IntegrationTest := false
  )

lazy val play = project.in(file("play"))
  .settings(commonSettings)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(
    name := "rrt-play",
    libraryDependencies ++= Dependencies.play
  )
  .dependsOn(core)

lazy val plugin = project.in(file("plugin"))
  .settings(commonSettings)
  .settings(
    name := "rrt-plugin",
    scalaVersion  := "2.10.6",
    scalacOptions += "-target:jvm-1.7",
    sbtPlugin := true
  )
