package com.github.pheymann.rrtt

import com.github.pheymann.rrtt.TestAction.{DoubleData, GetTestCase, IntData, PositiveIntData, StaticData}
import org.specs2.mutable.Specification

class RandomValueGeneratorItSpec extends Specification {

  sequential

  val testName = "random-value-it-spec"
  val testConfig = newConfig(testName, "127.0.0.1", 9000, "127.0.0.1", 9001).withRepetitions(10)

  "The library api" should {
    "provide an action to select values from a given static `List` randomly" in new WithTestServices(testConfig) {
      val testCase = for {
        statics <- StaticData(List("Luke", "Boba", "Yoda", "Anakin", "Han", "C3PO")).lift
        result  <- GetTestCase { _ =>
          val uri = s"/hello/${statics()}"

          uri |+| Map.empty[String, String]
        }.lift
      } yield result

      checkAndLog(testCase.runCase(testConfig)) should beTrue
    }

    "provide an action to create random `Int` and positive `Int` generators" in new WithTestServices(testConfig) {
      val testCase = for {
        ints          <- IntData(Some(10)).lift
        positiveInts  <- PositiveIntData().lift
        result        <- GetTestCase { rand =>
          val uri = s"/add/${ints()}/and/${ints()}"
          val params = rand.nextOptPair("offset", positiveInts)

          uri |+| params
        }.lift
      } yield result

      checkAndLog(testCase.runCase(testConfig)) should beTrue
    }

    "provide an action to create random `Double` generators" in new WithTestServices(testConfig) {
      val testCase = for {
        doubles <- DoubleData(Some(10.0)).lift
        result  <- GetTestCase { _ =>
          val uri = s"/multiply/${doubles()}/and/${doubles()}"

          uri |+| Map.empty[String, String]
        }.lift
      } yield result

      checkAndLog(testCase.runCase(testConfig)) should beTrue
    }
  }

}
