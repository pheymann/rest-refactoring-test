package com.github.pheymann.rrt

import com.github.pheymann.rrt.util.RandomUtil

import scala.collection.mutable

final case class ParamsBuilder(params: mutable.Builder[(String, String), Map[String, String]]) {

  private def add(key: String, value: String): ParamsBuilder = {
    this.params += key -> value
    this
  }

  private def addOpt(key: String, value: String)(implicit rand: RandomUtil): ParamsBuilder = {
    if (rand.rand.nextBoolean())
      add(key, value)

    this
  }

  def add[A](key: String, value: A): ParamsBuilder = add(key, value.toString)
  def addOpt[A](key: String, value: A)(implicit rand: RandomUtil): ParamsBuilder = addOpt(key, value.toString)

  def add[A](key: String, value: () => A): ParamsBuilder = add(key, value().toString)
  def addOpt[A](key: String, value: () => A)(implicit rand: RandomUtil): ParamsBuilder = addOpt(key, value().toString)

  def add[A](key: String, value: Seq[A]): ParamsBuilder = add(key, value.mkString(","))
  def addOpt[A](key: String, value: Seq[A])(implicit rand: RandomUtil): ParamsBuilder = addOpt(key, value.mkString(","))

}

trait ParamsBuilderSyntax {

  object Params {

    def apply(): ParamsBuilder = ParamsBuilder(Map.newBuilder[String, String])
    def apply(params: mutable.Builder[(String, String), Map[String, String]]): ParamsBuilder = ParamsBuilder(params)

  }

}

object ParamsBuilderSyntax extends ParamsBuilderSyntax
