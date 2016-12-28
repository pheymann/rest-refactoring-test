package com.github.pheymann.rrtt

import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import com.github.pheymann.rrtt.TestRunner.GetRequest
import com.github.pheymann.rrtt.util.RandomUtil
import com.github.pheymann.rrtt.util.ResponseComparator.ComparisonResult
import org.specs2.mutable.Specification

import scala.concurrent.Future

class TestRunnerSpec extends Specification {

  sequential

  val testConfig = newConfig("test-runner-spec", "", 80, "", 80)

  "The TestRunner" should {
    """be able to execute a test case sequential by sending requests to the actual and the
      |expected service, comparing the responses and repeat this step n times. The comparison
      |results are collected and returned.""".stripMargin in new WithActorSystem {
      implicit val materializer = ActorMaterializer()

      val testRequest = GetRequest("--", Map.empty)

      val testRest0 = () => Future.successful(
        testRequest,
        HttpResponse(entity = "{\"id\":0}"),
        HttpResponse(entity = "{\"id\":0}")
      )

      TestRunner.runSequential(testConfig, RandomUtil, "TEST-GET")(testRest0) should beEqualTo(
        TestResult(testConfig.name, true, 1, 0, Nil)
      )

      val testRest1 = () => Future.successful(
        testRequest,
        HttpResponse(entity = "{\"id\":0}"),
        HttpResponse(entity = "{\"id\":1}")
      )

      TestRunner.runSequential(testConfig, RandomUtil, "TEST-GET")(testRest1) should beEqualTo(
        TestResult(testConfig.name, false, 0, 1, List(testRequest -> ComparisonResult(false, List(("body", "{\"id\":0}", "{\"id\":1}")))))
      )
    }

    "interrupt the test case execution if an exception occurs or/and a `Future` fails" in new WithActorSystem {
      implicit val materializer = ActorMaterializer()

      val testRest = () => Future.failed(new IllegalArgumentException("expected"))

      TestRunner.runSequential(testConfig, RandomUtil, "TEST-GET")(testRest) should beEqualTo(
        TestResult(testConfig.name, false, 0, 0, Nil)
      )
    }
  }

}
