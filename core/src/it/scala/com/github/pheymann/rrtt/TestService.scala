package com.github.pheymann.rrtt

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import org.slf4j.LoggerFactory

import scala.concurrent.Future

object TestService {

  import ContentTypes._

  val log = LoggerFactory.getLogger(getClass)

  def route(port: Int) =
    path("hello" / Segment) { name =>
      get {
        if (log.isDebugEnabled)
          log.debug(s"GET:$port /hello - name = $name")

        complete(HttpEntity(`text/html(UTF-8)`, s"<h2>hello $name</h2>"))
      }
    } ~
    path("add" / IntNumber / "and" / IntNumber) { (a, b) =>
      get {
        parameters("offset".as[Int] ?) { offset =>
          if (log.isDebugEnabled)
            log.debug(s"GET:$port /add - offset = $offset; a = $a; b = $b")

          complete(HttpEntity(`text/html(UTF-8)`, (a + b + offset.getOrElse(0)).toString))
        }
      }
    } ~
    path("multiply" / DoubleNumber / "and" / DoubleNumber) { (a, b) =>
      get {
        if (log.isDebugEnabled)
          log.debug(s"GET:$port /multiply - a = $a; b = $b")

        complete(HttpEntity(`text/html(UTF-8)`, (a * b).toString))
      }
    }

  def run(port: Int)
         (implicit system: ActorSystem): Future[ServerBinding] = {
    implicit val materializer = ActorMaterializer()

    Http().bindAndHandle(route(port), "localhost", port)
  }

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("example-service")

    run(9000)
  }

}