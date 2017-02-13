package com.github.pheymann.rrt.util

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import akka.stream.Materializer
import com.github.pheymann.rrt.TestConfig

import scala.concurrent.Future

object ResponseComparator {

  sealed trait ComparisonFailure {

    def toLog: String

  }

  final case class FailureWithValues(element: String, actual: String, expected: String) extends ComparisonFailure {

    override def toLog: String = {
      s"${this.element}:\n  actual:   " + Console.RED + this.actual + Console.WHITE + "\n  expected: " + Console.RED + this.expected + Console.WHITE
    }

  }
  final case class FailureWithDiffs(element: String, diffs: String) extends ComparisonFailure {

    override def toLog: String = {
      s"${this.element}:\n  differences: \n" + Console.RED + this.diffs + Console.WHITE + "\n"
    }

  }

  type BodyComparison = (String, String, TestConfig) => Option[ComparisonFailure]

  final case class ComparisonResult(areEqual: Boolean, failures: List[ComparisonFailure])

  def compareResponses(actual: HttpResponse, expected: HttpResponse, compare: BodyComparison, config: TestConfig)
                      (implicit system: ActorSystem, materializer: Materializer): Future[ComparisonResult] = {
    import system.dispatcher

    compareBodies(actual, expected, compare, config).map { failedBodyOpt =>
      val failures = List(
        compareStatus(actual, expected, config),
        failedBodyOpt
      ).flatten

      ComparisonResult(failures.isEmpty, failures)
    }
  }

  private[util] def compareStatus(actual: HttpResponse, expected: HttpResponse, config: TestConfig): Option[ComparisonFailure] = {
    if (actual.status != expected.status)
      Some(FailureWithValues("status", actual.status.toString, expected.status.toString))
    else if (!config.ignoreStatusFailure && actual.status.isFailure)
      Some(FailureWithValues("status failure", actual.status.toString, expected.status.toString))
    else
      None
  }

  private def compareBodies(actual: HttpResponse,
                            expected: HttpResponse,
                            compare: BodyComparison,
                            config: TestConfig)
                           (implicit
                            system: ActorSystem,
                            materializer: Materializer): Future[Option[ComparisonFailure]] = {
    import system.dispatcher

    for {
      actualBody    <- actual.entity.toStrict(config.timeout).map(_.data.decodeString("UTF-8"))
      expectedBody  <- expected.entity.toStrict(config.timeout).map(_.data.decodeString("UTF-8"))
    } yield compare(actualBody, expectedBody, config)
  }

}

object BodyComparison {

}
