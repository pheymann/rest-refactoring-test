package com.github.pheymann.rrt.util

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer
import com.github.pheymann.rrt._
import com.github.pheymann.rrt.util.ResponseComparator.{ComparisonResult, FailureWithValues}
import com.github.pheymann.rrt.WithActorSystem
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification

class ResponseComparatorSpec(implicit ee: ExecutionEnv) extends Specification {

  sequential

  val testConfig = newConfig("comparator-spec", "", 80, "", 80)

  "The ResponseComparator" should {
    "compare the HttpResponses and document all differences" in new WithActorSystem {
      implicit val materializer = ActorMaterializer()

      val actualResponse = HttpResponse(entity = "{\"id\":0}")

      ResponseComparator.compareResponses(
        actualResponse,
        actualResponse,
        BodyAsStringComparison.stringComparison,
        testConfig
      ) should beEqualTo(ComparisonResult(true, Nil)).awaitFor(testConfig.timeout)

      ResponseComparator.compareResponses(
        actualResponse,
        HttpResponse(status = StatusCodes.NotFound, entity = "{\"id\":0}"),
        BodyAsStringComparison.stringComparison,
        testConfig
      ) should beEqualTo(ComparisonResult(
        false,
        List(FailureWithValues("status", "200 OK", "404 Not Found"))
      )).awaitFor(testConfig.timeout)
    }
  }

}
