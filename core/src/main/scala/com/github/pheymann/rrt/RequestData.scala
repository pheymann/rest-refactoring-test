package com.github.pheymann.rrt

import scala.language.implicitConversions

final case class RequestData(uri: String, params: Map[String, String], bodyOpt: Option[String] = None)

trait RequestDataSyntax {

  implicit def uriToRequestData(uri: String): RequestData = RequestData(uri, Map.empty)

  implicit class RequestDataBuilder(uri: String) {

    /** Creates a `RequestData` instance from the uri and params without a body.
      *
      * @param params query parameters
      * @return RequestData
      */
    def |+|(params: Map[String, String]): RequestData = RequestData(uri, params)

    /** Creates a `RequestData` instance from the uri and params without a body.
      *
      * @param builder builder for params
      * @return RequestData
      */
    def |+|(builder: ParamsBuilder): RequestData = RequestData(uri, builder.params.result())

    /** Creates a `RequestData` instance from the uri and body without params.
      *
      * @param body request body (entity)
      * @return RequestData
      */
    def |+|(body: String): RequestData = RequestData(uri, Map.empty, Some(body))

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
