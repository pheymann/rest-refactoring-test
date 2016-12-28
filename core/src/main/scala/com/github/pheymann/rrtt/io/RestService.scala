package com.github.pheymann.rrtt.io

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model._
import akka.stream.Materializer
import com.github.pheymann.rrtt.TestConfig

import scala.concurrent.Future

object RestService {

  private final val HttpPrefix = "http://"

  private def createHeaders(config: TestConfig): List[HttpHeader] = {
    config.headers.map {case (key, value) => RawHeader(key, value)}
  }

  private def completeUriStr(host: String, port: Int, uri: String): String = {
    val tmpUri = s"$host:$port$uri"

    if (host.contains(HttpPrefix))
      tmpUri
    else
      HttpPrefix + tmpUri
  }

  def getFromActual(uri: String, params: Map[String, String], config: TestConfig)
                   (implicit system: ActorSystem, materializer: Materializer): Future[HttpResponse] = {
    import config.actual._

    val completeUri = completeUriStr(host, port, uri)

    Http().singleRequest(HttpRequest(uri = Uri(completeUri).withQuery(Query(params)), headers = createHeaders(config)))
  }

  def getFromExpected(uri: String, params: Map[String, String], config: TestConfig)
                     (implicit system: ActorSystem, materializer: Materializer): Future[HttpResponse] = {
    import config.expected._

    val completeUri = completeUriStr(host, port, uri)

    Http().singleRequest(HttpRequest(uri = Uri(completeUri).withQuery(Query(params)), headers = createHeaders(config)))
  }

  def postFromActual(uri: String, params: Map[String, String], body: String, config: TestConfig)
                    (implicit system: ActorSystem, materializer: Materializer): Future[HttpResponse] = {
    import config.actual._

    val completeUri = completeUriStr(host, port, uri)

    Http().singleRequest(HttpRequest(HttpMethods.POST, Uri(completeUri).withQuery(Query(params)), createHeaders(config), entity = body))
  }

  def postFromExpected(uri: String, params: Map[String, String], body: String, config: TestConfig)
                      (implicit system: ActorSystem, materializer: Materializer): Future[HttpResponse] = {
    import config.expected._

    val completeUri = completeUriStr(host, port, uri)

    Http().singleRequest(HttpRequest(HttpMethods.POST, Uri(completeUri).withQuery(Query(params)), createHeaders(config), entity = body))
  }

}
