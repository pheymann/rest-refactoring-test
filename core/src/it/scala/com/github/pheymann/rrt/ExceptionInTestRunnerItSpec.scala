package com.github.pheymann.rrt

import org.specs2.mutable.Specification

class ExceptionInTestRunnerItSpec extends Specification {

  val testName = "test-runner-exception-it-spec"
  val testConfig = newConfig(testName, "127.0.0.1", 10000, "127.0.0.1", 10001)
    .withRepetitions(1)

  "If an `Exception` occurs in `TestRunner` it" should {
    "mark the test case as failed and stop the process" in new WithTestServices(testConfig) {
      val testCase = testGet(_ => throw new IllegalArgumentException("expected"))

      checkAndLog(testCase.runSeq(testConfig)) should beFalse
    }
  }

}
