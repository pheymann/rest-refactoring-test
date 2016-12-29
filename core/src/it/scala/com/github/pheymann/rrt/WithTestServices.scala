package com.github.pheymann.rrt

import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.testkit.TestKit
import org.specs2.mutable.BeforeAfter

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

abstract class WithTestServices(config: TestConfig) extends TestKit(ActorSystem()) with BeforeAfter {

  var actualFut: Future[ServerBinding] = _
  var expectedFut: Future[ServerBinding] = _

  override def before = {
    actualFut = TestService.run(config.actual.port)
    expectedFut = TestService.run(config.expected.port)
  }

  override def after = {
    import system.dispatcher

    val shutdownFut = for {
      actual    <- actualFut
      expected  <- expectedFut

      actualUnbound   <- actual.unbind()
      expectedUnbound <- expected.unbind()

      terminated <- system.terminate()
    } yield terminated

    Await.result(shutdownFut, 5.seconds)
  }

}
