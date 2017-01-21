package com.github.pheymann.rrt

import org.specs2.mutable.Specification

class RequestThrottlingItSpec extends Specification {

  val testName = "request-throttling-it-spec"

  val serverConfig = newConfig(testName, "127.0.0.1", 10000, "127.0.0.1", 10001)

  "The library" should {
    "provide a config to throttle the maximum number of request" in new WithTestServices(serverConfig) {
      val testCase = for {
        names   <- genStaticData("Luke", "Boba", "Yoda", "Anakin", "Han", "C3PO", "R2D2", "ObiWan", "Padme", "Leia")
        result  <- testGet { _ =>
          s"/hello/${names()}"
        }
      } yield result

      //warm-up
      testCase.runSeq(serverConfig.withRepetitions(10000))

      val t0Fast = System.currentTimeMillis()
      val testConfigFast = serverConfig
        .withRepetitions(10)
        .withThrottling(10)

      testCase.runSeq(testConfigFast)
      val tDiffFast = System.currentTimeMillis() - t0Fast

      val t0Slow = System.currentTimeMillis()
      val testConfigSlow = serverConfig
        .withRepetitions(10)
        .withThrottling(1)

      testCase.runSeq(testConfigSlow)
      val tDiffSlow = System.currentTimeMillis() - t0Slow

      println(s"fast: $tDiffFast ms - slow: $tDiffSlow ms")
      tDiffFast < tDiffSlow
    }
  }

}
