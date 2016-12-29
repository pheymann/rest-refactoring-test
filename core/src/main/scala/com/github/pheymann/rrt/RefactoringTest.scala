package com.github.pheymann.rrt

import akka.actor.ActorSystem
import cats.free.Free
import org.slf4j.LoggerFactory

trait RefactoringTest {

  def runTestCase(testCase: Free[TestAction, TestResult], config: TestConfig)
                 (implicit system: ActorSystem): TestResult = {
    testCase.foldMap(TestActionInterpreter.interpreter(config))
  }

  def checkResult(result: TestResult): Boolean = {
    result.failedTries == 0
  }

  private final val PrintPrefix = "  "
  private val log = LoggerFactory.getLogger(getClass)

  def prettyLog(result: TestResult): Unit = {
    if (log.isInfoEnabled) {
      log.info(s"ran ${result.name} ${if (result.successful) "succeeded" else "failed"}:")
      log.info(s"$PrintPrefix succeeded tries: ${result.successfulTries}")
      log.info(s"$PrintPrefix failed tries:    ${result.failedTries}")

      if (!result.successful) {
        log.info(result.comparisons.mkString("\n"))
      }
      log.info("")
      log.info("")
    }
  }

  def checkAndLog(result: TestResult): Boolean = {
    prettyLog(result)
    checkResult(result)
  }

  implicit class RefactoringTestRun(testCase: Free[TestAction, TestResult]) {

    def runCase(config: TestConfig)
               (implicit system: ActorSystem): TestResult = runTestCase(testCase, config)

  }

}

object RefactoringTest extends RefactoringTest
