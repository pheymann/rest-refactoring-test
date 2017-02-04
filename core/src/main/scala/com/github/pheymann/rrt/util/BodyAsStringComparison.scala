package com.github.pheymann.rrt.util

import com.github.pheymann.rrt.TestConfig
import com.github.pheymann.rrt.util.ResponseComparator.{ComparisonFailure, FailureWithDiffs, FailureWithValues}

import scala.util.Try
import scala.util.control.NonFatal

object BodyAsStringComparison {

  def stringComparison(actual: String,
                       expected: String,
                       config: TestConfig): Option[ComparisonFailure] = {
    val actualCleanedByKey   = cleanBodyByKey(actual, config.jsonIgnore)
    val expectedCleanedByKey = cleanBodyByKey(expected, config.jsonIgnore)

    val actualCleaned   = cleanBodyByRegex(actualCleanedByKey, config.bodyRemovals)
    val expectedCleaned = cleanBodyByRegex(expectedCleanedByKey, config.bodyRemovals)

    if (config.showDiffs)
      jsonComparison(actualCleaned, expectedCleaned)
    else {
      if (actualCleaned != expectedCleaned)
        Some(FailureWithValues("body", actualCleaned, expectedCleaned))
      else
        None
    }
  }

  def jsonComparison(actual: String,
                     expected: String): Option[ComparisonFailure] = {
    import gnieh.diffson.playJson._

    try {
      val diffs = JsonDiff.diff(actual, expected, false).toString

      if (diffs != "[ ]")
        Some(FailureWithDiffs("body", diffs))
      else
        None
    } catch {
      case NonFatal(_) => Some(FailureWithValues("body", actual, expected))
    }
  }

  private final val EmptyReplacement = ""

  private[util] def cleanBodyByKey(body: String, keys: List[String]): String = {
    var cleanedBody = body

    def innerKeyRegex(key: String): String = "\"" + key + "\"\\s*:\\s*(\".*\")?([0-9]*)?(null)?(\\[.*\\])?(\\{.*\\})?\\s*,"
    def lastKeyRegex(key: String): String = "(,)?\"" + key + "\"\\s*:\\s*(\".*\")?([0-9]*)?(null)?(\\[.*\\])?(\\{.*\\})?\\s*\\}"

    for (key <- keys) {
      cleanedBody = cleanedBody.replaceAll(innerKeyRegex(key), EmptyReplacement)
      cleanedBody = cleanedBody.replaceAll(lastKeyRegex(key), "}")
    }
    cleanedBody
  }

  private[util] def cleanBodyByRegex(body: String, regexes: List[String]): String = {
    var cleanedBody = body

    for (regex <- regexes)
      cleanedBody = cleanedBody.replaceAll(regex, EmptyReplacement)
    cleanedBody
  }

}
