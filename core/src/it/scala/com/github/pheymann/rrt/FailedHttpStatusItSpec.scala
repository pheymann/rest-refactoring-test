package com.github.pheymann.rrt

import org.specs2.mutable.Specification

class FailedHttpStatusItSpec extends Specification {

  sequential

  val testName = "failed-http-status-it-spec"
  val testConfig = newConfig(testName, "127.0.0.1", 10000, "127.0.0.1", 10001)
    .withRepetitions(1)

  "When the response statuses are equal the ResponseComparator" should {
    "ignore status failures if set to ignore" in new WithTestServices(testConfig, actualFails = true, expectedFails = true) {
      val testCase = for {
        names   <- genStaticData("Anakin")
        result  <- testGet { _ =>
          s"/hello/${names()}"
        }
      } yield result

      checkAndLog(testCase.runSeq(testConfig.ignoreStatusFailure(true))) should beTrue
    }

    "fail if http statuses are failures" in new WithTestServices(testConfig, actualFails = true, expectedFails = true) {
      val testCase = for {
        names   <- genStaticData("Anakin")
        result  <- testGet { _ =>
          s"/hello/${names()}"
        }
      } yield result

      checkAndLog(testCase.runSeq(testConfig)) should beFalse
    }
  }

}
