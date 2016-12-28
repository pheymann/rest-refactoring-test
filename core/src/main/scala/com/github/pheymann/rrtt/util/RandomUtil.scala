package com.github.pheymann.rrtt.util

import scala.util.Random

trait RandomUtil {

  def rand: Random

  /**
    * Generates an `Int` 0 < x.
    *
    * @return random `Int`
    */
  def nextPositiveInt(): Int = nextPositiveInt(Int.MaxValue)

  /**
    * Generates an `Int` 0 < x <= n.
    *
    * @param n upper border
    * @return random `Int`
    */
  def nextPositiveInt(n: Int): Int = {
    var result = rand.nextInt(n)

    while (result == 0)
      result = rand.nextInt(n)
    result
  }

  /**
    * Generates a `Long` values between 0 <= x <= n.
    *
    * @param n upper border
    * @return random `Long`
    */
  def nextLong(n: Long): Long = {
    def generator(): Long = (n * rand.nextDouble()).toLong
    var result = generator()

    while (result < 0)
      result = generator()
    result
  }

  def nextDouble(n: Double): Double = rand.nextDouble() * n

  def nextFromSeq[A](seq: Seq[A]): A = {
    seq(rand.nextInt(seq.length))
  }

  def nextOpt[A](next: () => A): Option[A] = {
    if (rand.nextBoolean) Some(next()) else None
  }

  def nextOptPair[A](key: String, next: () => A): Map[String, String] = {
    nextOpt(next).fold(Map.empty[String, String])(value => Map(key -> value.toString))
  }

}

object RandomUtil extends RandomUtil {

  override val rand = new Random(System.currentTimeMillis())

}
