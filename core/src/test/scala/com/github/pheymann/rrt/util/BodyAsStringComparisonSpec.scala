package com.github.pheymann.rrt.util

import com.github.pheymann.rrt._
import com.github.pheymann.rrt.util.ResponseComparator.{FailureWithDiffs, FailureWithValues}
import org.specs2.mutable.Specification

class BodyAsStringComparisonSpec extends Specification {

  import BodyAsStringComparison._

  val testConfig = newConfig("string-comparison-spec", "", 80, "", 80)

  "The body-as-string comparison" should {
    "clean the response entity (body), removing defined `String` segments by regex" in {
      val body = "{\"random_id\":23432j34343kkj3432,\"value\":\"hello world\"}"

      val bodyRemovals = List("\"random_id\":[0-9a-z]*,")

      cleanBodyByRegex(body, bodyRemovals) must beEqualTo("{\"value\":\"hello world\"}")
    }

    "clean the response Json (body) by defined keys" in {
      val body0 = "{\"test0\"  :\"aaaa\",\"test1\":\"bbbbbb\"}"

      cleanBodyByKey(body0, List("test0")) must beEqualTo("{\"test1\":\"bbbbbb\"}")

      val body1 = "{\"test0\"  :9845048,\"test1\":\"bbbbbb\"}"

      cleanBodyByKey(body1, List("test0")) must beEqualTo("{\"test1\":\"bbbbbb\"}")

      val body2 = "{\"test0\"  :null,\"test1\":\"bbbbbb\"}"

      cleanBodyByKey(body2, List("test0")) must beEqualTo("{\"test1\":\"bbbbbb\"}")

      val body3 = "{\"test0\"  :[0,1,2,\"a\"],\"test1\":\"bbbbbb\"}"

      cleanBodyByKey(body3, List("test0")) must beEqualTo("{\"test1\":\"bbbbbb\"}")

      val body4 = "{\"test0\"  :{\"a\":null},\"test1\":\"bbbbbb\"}"

      cleanBodyByKey(body4, List("test0")) must beEqualTo("{\"test1\":\"bbbbbb\"}")

      val body5 = "{\"test0\":{\"a\":null},\"test1\":\"bbbbbb\"}"

      cleanBodyByKey(body5, List("test1")) must beEqualTo("{\"test0\":{\"a\":null}}")

      val body6 = "{\"test0\":{\"a\":null}}"

      cleanBodyByKey(body6, List("test0")) must beEqualTo("{}")

      val body7 = "{\"test0\":{\"a\":null,\"b\":0},\"test1\":\"bbbbbb\",\"test2\":\"ccccc\"}"

      cleanBodyByKey(body7, List("test1")) must beEqualTo("{\"test0\":{\"a\":null,\"b\":0},\"test2\":\"ccccc\"}")

      val body8 = "{\"test0\":{\"a\":null,\"b\":0},\"test1\":\"bbbbbb\",\"test2\":{\"a\":null,\"b\":0}}"

      cleanBodyByKey(body8, List("test2")) must beEqualTo("{\"test0\":{\"a\":null,\"b\":0},\"test1\":\"bbbbbb\"}")
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
