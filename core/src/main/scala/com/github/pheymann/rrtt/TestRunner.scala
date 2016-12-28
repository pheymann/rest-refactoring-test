package com.github.pheymann.rrtt

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import akka.stream.{ActorMaterializer, Materializer}
import com.github.pheymann.rrtt.io.RestService
import com.github.pheymann.rrtt.util.ResponseComparator.ComparisonResult
import com.github.pheymann.rrtt.util.{RandomUtil, ResponseComparator}
import org.slf4j.LoggerFactory

import scala.concurrent.{Await, Future}
import scala.util.control.NonFatal

object TestRunner {

  private val log = LoggerFactory.getLogger(getClass)

  private[rrtt] def runSequential[R <: TestRequest](
                                                    config: TestConfig,
                                                    random: RandomUtil,
                                                    logHint: String
                                                   )
                                                   (rest: () => Future[(R, HttpResponse, HttpResponse)])
                                                   (implicit system: ActorSystem, materializer: Materializer): TestResult[R] = {
    import system.dispatcher

    if (log.isInfoEnabled)
      log.info(s"[$logHint] start ${config.name}")

    var round = 0
    var failed = false

    val comparisonsBuilder = List.newBuilder[(R, ComparisonResult)]

    while (round < config.repetitions && !failed) {
      try {
        comparisonsBuilder += Await.result({
            for {
              (request, actual, expected) <- rest()
              comparison <- ResponseComparator.compareResponses(actual, expected, config)
            } yield request -> comparison
          },
          config.timeout
        )
      } catch {
        case NonFatal(cause) =>
          log.error(s"[$logHint] failure in round = $round for ${config.name}", cause)
          failed = true
      }
      round += 1
    }

    if (failed)
      TestResult(config.name, !failed, 0, 0, Nil)
    else {
      val comparisons = comparisonsBuilder.result()
      val failedComparisons = comparisons.filterNot(_._2.areEqual)
      val failedTries = failedComparisons.length

      TestResult(config.name, failedTries == 0, config.repetitions - failedTries, failedTries, failedComparisons)
    }
  }

  sealed trait TestRequest

  final case class GetRequest(uri: String, params: Map[String, String]) extends TestRequest

  def runGetSequential(test: GetEndpointTestCase, config: TestConfig, random: RandomUtil)
                      (implicit system: ActorSystem): TestResult[GetRequest] = {
    import system.dispatcher

    implicit val materializer = ActorMaterializer()

    runSequential(config, random, "GET") { () =>
      val (uri, params) = test(random)

      for {
        testResponse <- RestService.getFromActual(uri, params, config)
        validationResponse <- RestService.getFromExpected(uri, params, config)
      } yield (GetRequest(uri, params), testResponse, validationResponse)
    }
  }

  final case class PostRequest(uri: String, params: Map[String, String], body: String) extends TestRequest

  def runPostSequential(test: PostEndpointTestCase, config: TestConfig, random: RandomUtil)
                       (implicit system: ActorSystem): TestResult[PostRequest] = {
    import system.dispatcher

    implicit val materializer = ActorMaterializer()

    runSequential(config, random, "POST") { () =>
      val (uri, params, body) = test(random)

      for {
        testResponse <- RestService.postFromActual(uri, params, body, config)
        validationResponse <- RestService.postFromExpected(uri, params, body, config)
      } yield (PostRequest(uri, params, body), testResponse, validationResponse)
    }
  }

}
