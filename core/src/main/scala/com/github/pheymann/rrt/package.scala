package com.github.pheymann

import com.github.pheymann.rrt.TestAction.RandomValueGen
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

  implicit class DataGeneratorToSeq[A](gen: RandomValueGen[A]) {

    def toSeq(size: Int): Seq[A] = (0 to size).map(_ => gen())
    def toSeq(maxSize: Int, rand: RandomUtil): Seq[A] = (0 to rand.nextPositiveInt(maxSize)).map(_ => gen())
    def toNonEmptySeq(maxSize: Int, rand: RandomUtil): Seq[A] = (0 to rand.nextPositiveInt(maxSize)).map(_ => gen())

  }

}
