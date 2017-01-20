package com.github.pheymann.rrt

import akka.actor.ActorSystem
import cats.free.Free
import com.github.pheymann.rrt.util.ResponseComparator.ComparisonResult

trait RefactoringTest {

  def runSequential(testCase: Free[TestAction, TestResult], config: TestConfig)
                   (implicit system: ActorSystem): TestResult = {
    testCase.foldMap(TestActionInterpreter.interpreter(config))
  }

  def checkResult(result: TestResult): Boolean = {
    result.failedTries == 0
  }

  private final val PrintPrefix = "  "

  private def toLog(request: RequestData, comparisonResult: ComparisonResult): String = {
    s"${request.uri}?${request.params.map { case (key, value) => s"$key=$value" }.mkString("&")}\n" +
      request.bodyOpt.fold("")(body => body + "\n") +
      comparisonResult.differences.map {
        case (element, actual, expected) =>
          s"$element:\n  actual:   " + Console.RED + actual + Console.WHITE + "\n  expected: " + Console.RED + expected + Console.WHITE
      }.mkString("\n\n")
  }

  def prettyLog(result: TestResult): Unit = {
    println(s"\ntest case ${result.name} ${if (result.successful) Console.GREEN + "succeeded" else Console.RED + "failed"}:" + Console.WHITE)
    println(Console.CYAN + s"$PrintPrefix successful tries: ${result.successfulTries}")

    print(s"$PrintPrefix failed tries:     ${result.failedTries}\n" + Console.WHITE)

    if (!result.successful) {
      println(result.comparisons.map((toLog _).tupled).mkString("\n\n"))
    }
  }

  def checkAndLog(result: TestResult): Boolean = {
    prettyLog(result)
    checkResult(result)
  }

  implicit class RefactoringTestRun(testCase: Free[TestAction, TestResult]) {

    def runSeq(config: TestConfig)
              (implicit system: ActorSystem): TestResult = runSequential(testCase, config)

  }

}

object RefactoringTest extends RefactoringTest
