package com.github.pheymann.rrt

import org.specs2.mutable.Specification

class CleanedResponseBodyItSpec extends Specification {

  sequential

  val testName = "cleaned-body-it-spec"
  val testConfig = newConfig(testName, "127.0.0.1", 9000, "127.0.0.1", 9001)

  "The response bodies" should {
    "be cleaned by Json key if at least one is set" in new WithTestServices(testConfig) {
      val testCase = for {
        statics <- genStaticData("Luke", "Boba", "Yoda", "Anakin", "Han", "C3PO")
        result  <- testGet { _ =>
          s"/hello/json/differ/${statics()}"
        }
      } yield result

      checkAndLog(testCase.runSeq(testConfig.withJsonIgnore(List("test")))) should beTrue
    }

    "be cleaned by regex if at least one is set" in new WithTestServices(testConfig) {
      val testCase = for {
        statics <- genStaticData("Luke", "Boba", "Yoda", "Anakin", "Han", "C3PO")
        result  <- testGet { _ =>
          s"/hello/json/differ/${statics()}"
        }
      } yield result

      checkAndLog(testCase.runSeq(testConfig.withIgnoreByRegex(List("\"test\":[0-9]*")))) should beTrue
    }

    "be cleaned by regex and Json key" in new WithTestServices(testConfig) {
      val testCase = for {
        statics <- genStaticData("Luke", "Boba", "Yoda", "Anakin", "Han", "C3PO")
        result  <- testGet { _ =>
          s"/hello/json/differ/${statics()}"
        }
      } yield result

      checkAndLog(testCase.runSeq(testConfig
        .withIgnoreByRegex(List("\"test\":[0-9]*"))
        .withJsonIgnore(List("message"))
      )) should beTrue
    }
  }

}
