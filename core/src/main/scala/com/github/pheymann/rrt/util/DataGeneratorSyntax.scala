package com.github.pheymann.rrt.util

import com.github.pheymann.rrt.TestAction._
import com.github.pheymann.rrt._

trait DataGeneratorSyntax {

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

object DataGeneratorSyntax extends DataGeneratorSyntax
