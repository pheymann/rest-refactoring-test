package com.github.pheymann.rrt

import sbt.Keys._
import sbt.{Defaults, _}

package object plugin {

  val RefactoringTest = config("rt") extend Test

  lazy val playRtSettings = inConfig(RefactoringTest)(Defaults.testSettings) ++ Seq(
    scalaSource       in RefactoringTest := baseDirectory.value / "rt",
    resourceDirectory in RefactoringTest := baseDirectory.value / "test-conf"
  )

}
