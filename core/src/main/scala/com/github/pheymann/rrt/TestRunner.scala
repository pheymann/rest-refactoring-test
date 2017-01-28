package com.github.pheymann.rrt

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpMethod, HttpMethods, HttpResponse}
import akka.stream.{ActorMaterializer, Materializer}
import com.github.pheymann.rrt.io.RestService
import com.github.pheymann.rrt.util.ResponseComparator.{BodyComparison, ComparisonResult}
import com.github.pheymann.rrt.util.{RandomUtil, ResponseComparator}

import scala.concurrent.{Await, Future}
import scala.util.control.NonFatal

object TestRunner {

  private[rrt] def runSequential(comparison: BodyComparison,
                                 config: TestConfig,
                                 random: RandomUtil,
                                 logHint: String)
                                (rest: () => Future[(RequestData, HttpResponse, HttpResponse)])
                                (implicit system: ActorSystem, materializer: Materializer): TestResult = {
    import system.dispatcher

    println(s"[$logHint] start ${config.name}\n")

    var round = 0
    var failed = false
    var printedPercentage = -1

    val comparisonsBuilder = List.newBuilder[(RequestData, ComparisonResult)]

    // time window for a single request in milliseconds
    val requestWindowOpt = config.requestPerSecondOpt.map(r => ((1.0 / r) * 1000.0).toLong)

    while (round < config.repetitions && !failed) {
      val t0 = System.currentTimeMillis()

      try {
        comparisonsBuilder += Await.result({
            for {
              (data, actual, expected) <- rest()
              comparison <- ResponseComparator.compareResponses(actual, expected, comparison, config)
            } yield data -> comparison
          },
          config.timeout
        )

        printedPercentage = ProgressOutput.printProgress(round, config.repetitions, printedPercentage)
      } catch {
        case NonFatal(cause) =>
          println(s"\n[$logHint] failure in round = $round for ${config.name}")
          cause.printStackTrace()
          failed = true
      }
      round += 1

      requestWindowOpt.foreach { window =>
        val tDiff = System.currentTimeMillis() - t0

        if (tDiff < window)
          Thread.sleep(window - tDiff)
      }
    }

    ProgressOutput.printProgress(config.repetitions, config.repetitions, printedPercentage)
    println("")

    val comparisons = comparisonsBuilder.result()
    val failedComparisons = comparisons.filterNot(_._2.areEqual)
    val failedTries = failedComparisons.length

    TestResult(config.name, !failed && failedTries == 0, config.repetitions - failedTries, failedTries, failedComparisons)
  }

  private def requestServices(method: HttpMethod,
                              test: EndpointTestCase,
                              bodyComp: BodyComparison,
                              config: TestConfig,
                              random: RandomUtil)
                             (implicit system: ActorSystem): TestResult = {
    import system.dispatcher

    implicit val materializer = ActorMaterializer()

    runSequential(bodyComp, config, random, method.value) { () =>
      val data = test(random)

      for {
        testResponse <- RestService.requestFromActual(method, data, config)
        validationResponse <- RestService.requestFromExpected(method, data, config)
      } yield (data, testResponse, validationResponse)
    }
  }

  def runGetSequential(test: EndpointTestCase, bodyComp: BodyComparison, config: TestConfig, random: RandomUtil)
                      (implicit system: ActorSystem): TestResult = {
    requestServices(HttpMethods.GET, test, bodyComp, config, random)
  }

  def runPostSequential(test: EndpointTestCase, bodyComp: BodyComparison, config: TestConfig, random: RandomUtil)
                       (implicit system: ActorSystem): TestResult = {
    requestServices(HttpMethods.POST, test, bodyComp, config, random)
  }

  def runPutSequential(test: EndpointTestCase, bodyComp: BodyComparison, config: TestConfig, random: RandomUtil)
                      (implicit system: ActorSystem): TestResult = {
    requestServices(HttpMethods.PUT, test, bodyComp, config, random)
  }

  def runDeleteSequential(test: EndpointTestCase, bodyComp: BodyComparison, config: TestConfig, random: RandomUtil)
                         (implicit system: ActorSystem): TestResult = {
    requestServices(HttpMethods.DELETE, test, bodyComp, config, random)
  }

}
