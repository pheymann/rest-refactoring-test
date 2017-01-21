package com.github.pheymann.rrt

import org.specs2.mutable.Specification

class ParamsBuilderSpec extends Specification {

  "The ParamsBuilder and its syntax" should {
    "provide a function to add key -> value pairs of any value type (uses `toString`)" in {
      Params().add("key", "value").add("key2", 3).params.result() should beEqualTo(Map("key" ->  "value", "key2" -> "3"))
    }

    "provide a function to add key -> generator pairs of any generator type (uses `toString` for resulting values)" in {
      Params().add("key", () => "value").add("key2", () => 3).params.result() should beEqualTo(Map("key" ->  "value", "key2" -> "3"))
    }

    "provide a function to add key -> `Seq` pairs of any `Seq` type (uses `mkString` for resulting values)" in {
      Params().add("key", Seq("1", "2")).add("key2", Seq(1, 2)).params.result() should beEqualTo(Map("key" ->  "1,2", "key2" -> "1,2"))
    }
  }

}
