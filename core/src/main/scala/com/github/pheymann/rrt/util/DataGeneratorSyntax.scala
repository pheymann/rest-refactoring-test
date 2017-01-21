package com.github.pheymann.rrt.util

import com.github.pheymann.rrt.TestAction._
import com.github.pheymann.rrt._

trait DataGeneratorSyntax {

  implicit class DataGeneratorToOpt[A](gen: RandomValueGen[A]) {

    /** Wrappes a `RandomValueGen` result into a `Option`. If it
      * is a `Some` or `None` is decided randomly.
      *
      * @param rand implicit `RandomUtil`
      * @return either a Some(value: A) or a None
      */
    def toOpt(implicit rand: RandomUtil): Option[A] = {
      if (randBoolean)
        Some(gen())
      else
        None
    }

  }

  implicit class DataGeneratorToSeq[A](gen: RandomValueGen[A]) {

    /** Generates a `Seq` of values from `RandomValueGen` with a defined
      * maximum size.
      *
      * @param maxSize Seq.length <= maxSize
      * @param rand implicit `RandomUtil`
      * @return a `Seq` of random values: A with random but bounded size
      */
    def toSeq(maxSize: Int)
             (implicit rand: RandomUtil): Seq[A] = (0 to rand.nextPositiveInt(maxSize)).map(_ => gen())

    /** Generates a `Seq` of values from `RandomValueGen` with a defined
      * maximum size and at least one element.
      *
      * @param maxSize 0 < Seq.length <= maxSize
      * @param rand implicit `RandomUtil`
      * @return a `Seq` of random values: A with random but bounded size
      */
    def toNonEmptySeq(maxSize: Int)
                     (implicit rand: RandomUtil): Seq[A] = (0 to rand.nextPositiveInt(maxSize)).map(_ => gen())

  }

}

object DataGeneratorSyntax extends DataGeneratorSyntax
