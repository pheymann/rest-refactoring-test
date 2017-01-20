package com.github.pheymann

import com.github.pheymann.rrt.TestAction.RandomValueGen
import com.github.pheymann.rrt.util.{RandomSyntax, RandomUtil}

package object rrt extends RefactoringTest with RequestDataSyntax with TestActionSyntax with RandomSyntax {

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

  implicit class DataGeneratorToOpt[A](gen: RandomValueGen[A]) {

    def toOpt(implicit rand: RandomUtil): Option[A] = {
      if (randBoolean)
        Some(gen())
      else
        None
    }

  }

  implicit class DataGeneratorToSeq[A](gen: RandomValueGen[A]) {

    def toSeq(maxSize: Int)
             (implicit rand: RandomUtil): Seq[A] = (0 to rand.nextPositiveInt(maxSize)).map(_ => gen())

    def toNonEmptySeq(maxSize: Int)
                     (implicit rand: RandomUtil): Seq[A] = (0 to rand.nextPositiveInt(maxSize)).map(_ => gen())

  }

}
