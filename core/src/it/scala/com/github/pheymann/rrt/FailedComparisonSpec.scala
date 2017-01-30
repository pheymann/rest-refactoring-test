package com.github.pheymann.rrt

import org.specs2.mutable.Specification

class FailedComparisonSpec extends Specification {

  sequential

  val testName = "failed-comparison-it-spec"
  val testConfig = newConfig(testName, "127.0.0.1", 10000, "127.0.0.1", 10001)
    .withRepetitions(1)

  "When a comparison failed it" should {
    "print either the values of the two response bodies and mark the test as failed" in new WithTestServices(testConfig, actualFails = true) {
      val testCase = for {
        names   <- genStaticData("Anakin")
        result  <- testGet { _ =>
          s"/hello/${names()}"
        }
      } yield result

      checkAndLog(testCase.runSeq(testConfig)) should beFalse
    }

    "print either the values of the two response bodies and mark the test as failed" in new WithTestServices(testConfig, actualFails = true) {
      val testCase = for {
        names   <- genStaticData("Anakin")
        result  <- testGet { _ =>
          s"/hello/json/${names()}"
        }
      } yield result

      checkAndLog(testCase.runSeq(testConfig.showJsonDiffs(true))) should beFalse
    }
  }

}
