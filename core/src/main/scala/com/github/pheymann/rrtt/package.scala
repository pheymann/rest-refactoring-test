package com.github.pheymann

import com.github.pheymann.rrtt.util.RandomUtil

package object rrtt extends RefactoringTest with RequestDataSyntax {

  type EndpointTestCase = (RandomUtil => RequestData)

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
