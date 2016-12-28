package com.github.pheymann

import com.github.pheymann.rrtt.util.RandomUtil

package object rrtt extends RefactoringTest {

  type GetEndpointTestCase = (RandomUtil => (String, Map[String, String]))
  type PostEndpointTestCase = (RandomUtil => (String, Map[String, String], String))

  def newConfig(
                 name: String,
                 actualHost: String,
                 actualPort: Int,
                 expectedHost: String,
                 expectedPort: Int
               ): TestConfig = {
    TestConfig(
      name,
      ServiceConfig(actualHost, actualPort),
      ServiceConfig(expectedHost, expectedPort)
    )
  }

}
