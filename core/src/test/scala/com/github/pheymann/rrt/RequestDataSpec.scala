package com.github.pheymann.rrt

import akka.http.scaladsl.model.ContentTypes
import org.specs2.mutable.Specification

class RequestDataSpec extends Specification {

  "The RequestData" should {
    "provide syntax to compose an instance from uri and/or params and/or a body" in {
      import ContentTypes._

      val uri = "/rest/test"
      val params = Map("test" -> "param")
      val body = """{"test": "body"}"""

      (uri |+| params) should beEqualTo(RequestData(uri, params))
      (uri |+| params |+| body) should beEqualTo(RequestData(uri, params, Some(body)))
      (uri |+| body) should beEqualTo(RequestData(uri, Map.empty, Some(body)))
      (uri |+| body |+| body) should beEqualTo(RequestData(uri, Map.empty, Some(body)))
      (uri |+| body |+| body |=| `application/json`) should beEqualTo(RequestData(uri, Map.empty, Some(body), Some(`application/json`)))

      uriToRequestData(uri) should beEqualTo(RequestData(uri, Map.empty))
    }
  }

}
