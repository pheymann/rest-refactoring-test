package com.github.pheymann.rrt.util

trait RandomSyntax {

  def randBoolean(implicit rand: RandomUtil): Boolean = rand.rand.nextBoolean()
  def randFloat(implicit rand: RandomUtil): Float = rand.rand.nextFloat()
  def randInt(implicit rand: RandomUtil): Int = rand.rand.nextInt()
  def randInt(n: Int)(implicit rand: RandomUtil): Int = rand.rand.nextInt(n)
  def randLong(implicit rand: RandomUtil): Long = rand.rand.nextLong()
  def randDouble(implicit rand: RandomUtil): Double = rand.rand.nextDouble()
  def randDouble(n: Double)(implicit rand: RandomUtil): Double = n * rand.rand.nextDouble()

}

object RandomSyntax extends RandomSyntax
