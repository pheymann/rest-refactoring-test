package com.github.pheymann.rrt.util

import com.github.pheymann.rrt.TestConfig
import com.github.pheymann.rrt.util.ResponseComparator.{ComparisonFailure, FailureWithDiffs, FailureWithValues}

object BodyAsStringComparison {

  def stringComparison(actual: String,
                       expected: String,
                       config: TestConfig): Option[ComparisonFailure] = {
    val actualCleanedBody   = cleanBody(actual, config.bodyRemovals)
    val expectedCleanedBody = cleanBody(expected, config.bodyRemovals)

    if (config.showDiffs)
      jsonComparison(actualCleanedBody, expectedCleanedBody)
    else {
      if (actualCleanedBody != expectedCleanedBody)
        Some(FailureWithValues("body", actualCleanedBody, expectedCleanedBody))
      else
        None
    }
  }

  def jsonComparison(actual: String,
                     expected: String): Option[ComparisonFailure] = {
    import gnieh.diffson.playJson._

    val diffs = JsonDiff.diff(actual, expected, false).toString

    if (diffs != "[ ]")
      Some(FailureWithDiffs("body", diffs))
    else
      None
  }

  private final val EmptyReplacement = ""

  private[util] def cleanBody(body: String, removals: List[String]): String = {
    var cleanedBody = body

    for (removal <- removals)
      cleanedBody = cleanedBody.replaceAll(removal, EmptyReplacement)
    cleanedBody
  }

}
