package com.github.pheymann.rrtt

final case class RequestData(uri: String, params: Map[String, String], bodyOpt: Option[String] = None)

trait RequestDataSyntax {

  implicit class RequestDataBuilder(uri: String) {

    def |+|(params: Map[String, String]): RequestData = RequestData(uri, params)

  }

  implicit class RequestDataExtender(request: RequestData) {

    def |+|(body: String): RequestData = request.copy(bodyOpt = Some(body))

  }

}

object RequestDataSyntax extends RequestDataSyntax
