package com.github.pheymann

import com.github.pheymann.rrt.util.RandomUtil

package object rrt extends RefactoringTest with RequestDataSyntax with TestActionSyntax {

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
