package com.github.pheymann.rrt

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import org.slf4j.LoggerFactory

import scala.concurrent.Future

object TestService {

  import ContentTypes._

  private val log = LoggerFactory.getLogger(getClass)

  private val errorResponse = HttpResponse(BadRequest, entity = "{\"error\":\"something went wrong\"}")

  private def route(port: Int, failing: Boolean) =
    path("hello" / Segment) { name =>
      get {
        if (log.isDebugEnabled)
          log.debug(s"GET:$port /hello - name = $name")

        complete {
          if (failing)
            errorResponse
          else
            HttpEntity(`text/html(UTF-8)`, s"<h2>hello $name</h2>")
        }
      }
    } ~
    path("hello" / "json" / Segment) { name =>
      get {
        if (log.isDebugEnabled)
          log.debug(s"GET:$port /hello/json - name = $name")

        complete {
          if (failing)
            errorResponse
          else
            HttpEntity(`text/html(UTF-8)`, "{\"message\": \"hello " + name + "\"}")
        }
      }
    } ~
    path("add" / IntNumber / "and" / IntNumber) { (a, b) =>
      get {
        parameters("offset".as[Int] ?) { offset =>
          if (log.isDebugEnabled)
            log.debug(s"GET:$port /add - offset = $offset; a = $a; b = $b")

          complete {
            if (failing)
              errorResponse
            else
              HttpEntity(`text/html(UTF-8)`, (a + b + offset.getOrElse(0)).toString)
          }
        }
      }
    } ~
    path("multiply" / DoubleNumber / "and" / DoubleNumber) { (a, b) =>
      get {
        if (log.isDebugEnabled)
          log.debug(s"GET:$port /multiply - a = $a; b = $b")

        complete {
          if (failing)
            errorResponse
          else
            HttpEntity(`text/html(UTF-8)`, (a * b).toString)
        }
      }
    }

  def run(port: Int, failing: Boolean)
         (implicit system: ActorSystem): Future[ServerBinding] = {
    implicit val materializer = ActorMaterializer()

    Http().bindAndHandle(route(port, failing), "localhost", port)
  }

}