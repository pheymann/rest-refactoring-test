package com.github.pheymann.rrt.util

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import akka.stream.Materializer
import com.github.pheymann.rrt.TestConfig

import scala.concurrent.Future

object ResponseComparator {

  final case class ComparisonResult(areEqual: Boolean, differences: List[(String, String, String)])

  def compareResponses(actual: HttpResponse, expected: HttpResponse, config: TestConfig)
                      (implicit system: ActorSystem, materializer: Materializer): Future[ComparisonResult] = {
    import system.dispatcher

    differentBodies(actual, expected, config).map { differenceOpt =>
      val differences = List(
        differentStatus(actual, expected),
        differenceOpt
      ).flatten

      ComparisonResult(differences.isEmpty, differences)
    }
  }

  private[util] def differentStatus(actual: HttpResponse, expected: HttpResponse): Option[(String, String, String)] = {
    if (actual.status != expected.status)
      Some(("status", actual.status.toString, expected.status.toString))
    else
      None
  }

  private[util] def differentBodies(
                                     actual: HttpResponse,
                                     expected: HttpResponse,
                                     config: TestConfig
                                   )(implicit system: ActorSystem, materializer: Materializer): Future[Option[(String, String, String)]] = {
    import system.dispatcher

    for {
      actualBody    <- actual.entity.toStrict(config.timeout).map(_.data.decodeString("UTF-8"))
      expectedBody  <- expected.entity.toStrict(config.timeout).map(_.data.decodeString("UTF-8"))

      actualCleanedBody   = cleanBody(actualBody, config.bodyRemovals)
      expectedCleanedBody = cleanBody(expectedBody, config.bodyRemovals)
    } yield {
      if (actualBody != expectedBody)
        Some("body", actualBody, expectedBody)
      else
        None
    }
  }

  private final val EmptyReplacement = ""

  private[util] def cleanBody(body: String, removals: List[String]): String = {
    var cleanedBody = body

    for (removal <- removals)
      cleanedBody = cleanedBody.replaceAll(removal, EmptyReplacement)
    cleanedBody
  }

}
