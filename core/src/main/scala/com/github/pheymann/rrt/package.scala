package com.github.pheymann

import com.github.pheymann.rrt.util.{DataGeneratorSyntax, RandomSyntax, RandomUtil}

package object rrt  extends RefactoringTest
                    with    RequestDataSyntax
                    with    TestActionSyntax
                    with    RandomSyntax
                    with    DataGeneratorSyntax {

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
