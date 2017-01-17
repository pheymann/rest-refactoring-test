package com.github.pheymann.rrt

import sbt.Keys._
import sbt.{Defaults, _}

package object plugin {

  val RestRefactoringTest = config("rrt") extend Test

  lazy val playRtSettings = inConfig(RestRefactoringTest)(Defaults.testSettings) ++ Seq(
    scalaSource       in RestRefactoringTest := baseDirectory.value / "rrt",
    resourceDirectory in RestRefactoringTest := baseDirectory.value / "test-conf"
  )

  val rrtVersion = "0.5.0-RC"

  val rrtCore = "com.github.pheymann" %% "rrt-core" % rrtVersion
  val rrtPlay = "com.github.pheymann" %% "rrt-play" % rrtVersion

}
