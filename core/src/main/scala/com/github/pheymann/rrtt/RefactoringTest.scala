package com.github.pheymann.rrtt

import akka.actor.ActorSystem
import cats.free.Free
import com.github.pheymann.rrtt.TestRunner.TestRequest
import org.slf4j.LoggerFactory

trait RefactoringTest {

  def runTestCase[R <: TestRequest](testCase: Free[TestAction, TestResult[R]], config: TestConfig)
                                   (implicit system: ActorSystem): TestResult[R] = {
    testCase.foldMap(TestActionInterpreter.interpreter(config))
  }

  def checkResult[R <: TestRequest](result: TestResult[R]): Boolean = {
    result.failedTries == 0
  }

  private final val PrintPrefix = "  "
  private val log = LoggerFactory.getLogger(getClass)

  def prettyLog[R <: TestRequest](result: TestResult[R]): Unit = {
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

  def checkAndLog[R <: TestRequest](result: TestResult[R]): Boolean = {
    prettyLog(result)
    checkResult(result)
  }

  implicit class RefactoringTestRun[R <: TestRequest](testCase: Free[TestAction, TestResult[R]]) {

    def runCase(config: TestConfig)
               (implicit system: ActorSystem): TestResult[R] = runTestCase(testCase, config)

  }

}

object RefactoringTest extends RefactoringTest
