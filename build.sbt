
val commonSettings = Seq(
  organization := "com.github.pheymann.rrt",
  scalaVersion := "2.11.8"
)

lazy val `rest-refactoring-test-tool` = project.in(file("."))
  .settings(commonSettings)
  .aggregate(core)

lazy val core = project.in(file("core"))
  .settings(commonSettings)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(
    name := "rrt-core",
    libraryDependencies ++= Dependencies.core,
    parallelExecution in IntegrationTest := false,

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