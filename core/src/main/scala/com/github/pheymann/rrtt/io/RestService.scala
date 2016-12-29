package com.github.pheymann.rrtt.io

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model._
import akka.stream.Materializer
import com.github.pheymann.rrtt.{RequestData, ServiceConfig, TestConfig}

import scala.concurrent.Future

object RestService {

  private final val HttpPrefix = "http://"

  private def createHeaders(headers: List[(String, String)]): List[HttpHeader] = {
    headers.map {case (key, value) => RawHeader(key, value)}
  }

  private def completeUriStr(host: String, port: Int, uri: String): String = {
    val tmpUri = s"$host:$port$uri"

    if (host.contains(HttpPrefix))
      tmpUri
    else
      HttpPrefix + tmpUri
  }

  private def requestService(method: HttpMethod,
                             data: RequestData,
                             headers: List[(String, String)],
                             config: ServiceConfig)
                            (implicit system: ActorSystem, materializer: Materializer): Future[HttpResponse] = {
    import config._
    import data._

    val completeUri = completeUriStr(host, port, uri)

    Http().singleRequest(HttpRequest(
      method,
      Uri(completeUri).withQuery(Query(params)),
      createHeaders(headers),
      entity = bodyOpt.fold(HttpEntity.Empty)(body => body)
    ))
  }

  def requestFromActual(method: HttpMethod,
                        data: RequestData,
                        config: TestConfig)
                       (implicit system: ActorSystem, materializer: Materializer): Future[HttpResponse] = {
    requestService(method, data, config.headers, config.actual)
  }

  def requestFromExpected(method: HttpMethod,
                          data: RequestData,
                          config: TestConfig)
                         (implicit system: ActorSystem, materializer: Materializer): Future[HttpResponse] = {
    requestService(method, data, config.headers, config.expected)
  }

}
