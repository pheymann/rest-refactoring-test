package com.github.pheymann.rrt

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.specs2.specification.After

import scala.concurrent.Await
import scala.concurrent.duration._

abstract class WithActorSystem extends TestKit(ActorSystem()) with After {

  override def after = {
    Await.result(system.terminate(), 5.seconds)
  }

}
