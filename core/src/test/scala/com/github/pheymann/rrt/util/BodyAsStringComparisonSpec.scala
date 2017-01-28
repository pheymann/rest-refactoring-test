package com.github.pheymann.rrt.util

import com.github.pheymann.rrt._
import com.github.pheymann.rrt.util.ResponseComparator.{FailureWithDiffs, FailureWithValues}
import org.specs2.mutable.Specification

class BodyAsStringComparisonSpec extends Specification {

  import BodyAsStringComparison._

  val testConfig = newConfig("string-comparison-spec", "", 80, "", 80)

  "The body-as-string comparison" should {
    "clean the response entity (body), removing defined `String` segments" in {
      val body = "{\"random_id\":23432j34343kkj3432,\"value\":\"hello world\"}"

      val bodyRemovals = List("\"random_id\":[0-9a-z]*,")

      cleanBody(body, bodyRemovals) must beEqualTo("{\"value\":\"hello world\"}")
    }

    "check if the response bodies are different" in {
      val actualBody = "{\"id\":0}"

      stringComparison(actualBody, actualBody, testConfig) should beEqualTo(None)
      stringComparison(actualBody, "{}", testConfig) should beEqualTo(Some(FailureWithValues("body", "{\"id\":0}", "{}")))
    }

    "collect the differences of two json strings" in {
      val actualBody = "{\"id\":0}"

      jsonComparison(actualBody, actualBody) should beEqualTo(None)
      jsonComparison(actualBody, "{\"id\":1}") should beEqualTo(Some(FailureWithDiffs(
        "body",
        "[ {\n  \"op\" : \"replace\",\n  \"path\" : \"/id\",\n  \"value\" : 1\n} ]"
      )))
    }
  }

}
