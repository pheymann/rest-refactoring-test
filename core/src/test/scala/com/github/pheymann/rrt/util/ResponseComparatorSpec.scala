package com.github.pheymann.rrt.util

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer
import com.github.pheymann.rrt._
import com.github.pheymann.rrt.util.ResponseComparator.ComparisonResult
import com.github.pheymann.rrt.WithActorSystem
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification

class ResponseComparatorSpec(implicit ee: ExecutionEnv) extends Specification {

  sequential

  val testConfig = newConfig("comparator-spec", "", 80, "", 80)

  "The ResponseComparator" should {
    "clean the response entity (body), removing defined `String` segments" in {
      val body =
        """
          |{
          |  "random_id": 23432j34343kkj3432,
          |  "value": "hello world"
          |}
        """.stripMargin

      val bodyRemovals = List("\"random_id\": [0-9a-z]*")

      ResponseComparator.cleanBody(body, bodyRemovals) must beEqualTo(
        """
          |{
          |  ,
          |  "value": "hello world"
          |}
        """.stripMargin)
    }

    "check if the response status are different" in {
      val actualResponse = HttpResponse(status = StatusCodes.OK)

      ResponseComparator.differentStatus(actualResponse, actualResponse) should beEqualTo(None)
      ResponseComparator.differentStatus(actualResponse, HttpResponse(status = StatusCodes.NotFound)) should beEqualTo(
        Some(("status", "200 OK", "404 Not Found"))
      )
    }

    "check if the response bodies are different" in new WithActorSystem {
      implicit val materializer = ActorMaterializer()

      val actualResponse = HttpResponse(entity = "{\"id\":0}")

      ResponseComparator.differentBodies(
        actualResponse,
        actualResponse,
        testConfig
      ) should beEqualTo(None).awaitFor(testConfig.timeout)
      ResponseComparator.differentBodies(
        actualResponse,
        actualResponse,
        testConfig.copy(bodyRemovals = List("\"id\""))
      ) should beEqualTo(None).awaitFor(testConfig.timeout)

      ResponseComparator.differentBodies(actualResponse, HttpResponse(entity = "{}"), testConfig) should beEqualTo(
        Some(("body", "{\"id\":0}", "{}"))
      ).awaitFor(testConfig.timeout)
    }

    "compare the HttpResponses and document all differences" in new WithActorSystem {
      implicit val materializer = ActorMaterializer()

      val actualResponse = HttpResponse(entity = "{\"id\":0}")

      ResponseComparator.compareResponses(
        actualResponse,
        actualResponse,
        testConfig
      ) should beEqualTo(ComparisonResult(true, Nil)).awaitFor(testConfig.timeout)

      ResponseComparator.compareResponses(
        actualResponse,
        HttpResponse(status = StatusCodes.NotFound, entity = "{\"id\":0}"),
        testConfig
      ) should beEqualTo(ComparisonResult(
        false,
        List(("status", "200 OK", "404 Not Found"))
      )).awaitFor(testConfig.timeout)
    }
  }

}
