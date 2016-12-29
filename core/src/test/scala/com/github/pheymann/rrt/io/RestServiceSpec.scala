package com.github.pheymann.rrt.io

import akka.http.scaladsl.model.headers.RawHeader
import org.specs2.mutable.Specification

class RestServiceSpec extends Specification {

  import RestService._

  "The RestService" should {
    "create `HttpHeader`s from a `List` of `String` `Tuple`s" in {
      createHeaders(Nil) should beEqualTo(Nil)
      createHeaders(("hello", "world") :: Nil) should beEqualTo(RawHeader("hello", "world") :: Nil)
    }

    "build an URI from host, port and given endpoint uri" in {
      // use `must` here as `should` is overloaded
      buildUriStr("localhost", 80, "/hello") must beEqualTo("http://localhost:80/hello")
      buildUriStr("http://localhost", 80, "/hello") must beEqualTo("http://localhost:80/hello")
    }
  }

}
