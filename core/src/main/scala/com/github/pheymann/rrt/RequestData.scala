package com.github.pheymann.rrt

final case class RequestData(uri: String, params: Map[String, String], bodyOpt: Option[String] = None)

trait RequestDataSyntax {

  def |+|(uri: String): RequestData = RequestData(uri, Map.empty)

  implicit class RequestDataBuilder(uri: String) {

    /** Creates a `RequestData` instance from the uri and params.
      *
      * @param params query parameters
      * @return RequestData
      */
    def |+|(params: Map[String, String]): RequestData = RequestData(uri, params)

  }

  implicit class RequestDataExtender(request: RequestData) {

    /** Adds a body to the `RequestData`.
      *
      * @param body request body (entity)
      * @return updated `RequestData`
      */
    def |+|(body: String): RequestData = request.copy(bodyOpt = Some(body))

  }

}

object RequestDataSyntax extends RequestDataSyntax
