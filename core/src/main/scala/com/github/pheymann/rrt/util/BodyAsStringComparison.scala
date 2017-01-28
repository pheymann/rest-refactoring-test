package com.github.pheymann.rrt.util

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import akka.stream.Materializer
import com.github.pheymann.rrt.TestConfig
import com.github.pheymann.rrt.util.ResponseComparator.{ComparisonFailure, FailureWithValues}

import scala.concurrent.Future

object BodyAsStringComparison {

  def compareBodies(actual: HttpResponse,
                    expected: HttpResponse,
                    config: TestConfig)
                   (implicit
                      system: ActorSystem,
                      materializer: Materializer): Future[Option[ComparisonFailure]] = {
    import system.dispatcher

    for {
      actualBody    <- actual.entity.toStrict(config.timeout).map(_.data.decodeString("UTF-8"))
      expectedBody  <- expected.entity.toStrict(config.timeout).map(_.data.decodeString("UTF-8"))

      actualCleanedBody   = cleanBody(actualBody, config.bodyRemovals)
      expectedCleanedBody = cleanBody(expectedBody, config.bodyRemovals)
    } yield {
      if (actualCleanedBody != expectedCleanedBody)
        Some(FailureWithValues("body", actualCleanedBody, expectedCleanedBody))
      else
        None
    }
  }

  def stringComparison(actual: String,
                       expected: String,
                       config: TestConfig): Option[ComparisonFailure] = {
    val actualCleanedBody   = cleanBody(actual, config.bodyRemovals)
    val expectedCleanedBody = cleanBody(expected, config.bodyRemovals)

    if (actualCleanedBody != expectedCleanedBody)
      Some(FailureWithValues("body", actualCleanedBody, expectedCleanedBody))
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
