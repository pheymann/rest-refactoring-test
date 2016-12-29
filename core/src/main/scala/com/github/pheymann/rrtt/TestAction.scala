package com.github.pheymann.rrtt

import cats.free.Free

sealed trait TestAction[R]

object TestAction {

  type RandomValueGen[A] = () => A

  implicit class TestActionLifter[R](action: TestAction[R]) {

    def lift: Free[TestAction, R] = Free.liftF(action)

  }

  final case class StaticData[A](data: List[A]) extends TestAction[RandomValueGen[A]]

  final case class IntData(maxOpt: Option[Int] = None) extends TestAction[RandomValueGen[Int]]
  final case class PositiveIntData(maxOpt: Option[Int] = None) extends TestAction[RandomValueGen[Int]]
  final case class LongData(maxOpt: Option[Long] = None) extends TestAction[RandomValueGen[Long]]
  final case class DoubleData(maxOpt: Option[Double] = None) extends TestAction[RandomValueGen[Double]]

  sealed trait DbReadyTestAction[R]

  final case class FromDatabase[R](
                                    table:     String,
                                    pkColumn:  String,
                                    resultColumn: String,
                                    action: DbReadyTestAction[R]
                                  ) extends TestAction[R]

  implicit class WithFromDatabase[R](action: DbReadyTestAction[R]) {

    def fromDb(table:     String,
               selectCol: String,
               resultCol: String): Free[TestAction, R] = FromDatabase(table, selectCol, resultCol, action).lift

  }

  final case class RetrieveInts(size: Int) extends DbReadyTestAction[RandomValueGen[Int]]
  final case class RetrieveLongs(size: Int) extends DbReadyTestAction[RandomValueGen[Long]]
  final case class RetrieveDoubles(size: Int) extends DbReadyTestAction[RandomValueGen[Double]]

  final case class RetrieveStrings(size: Int) extends DbReadyTestAction[RandomValueGen[String]]

  final case class GetTestCase(test: EndpointTestCase) extends TestAction[TestResult]

  def get(test: EndpointTestCase): Free[TestAction, TestResult] = GetTestCase(test).lift

  final case class PostTestCase(test: EndpointTestCase) extends TestAction[TestResult]

  def post(test: EndpointTestCase): Free[TestAction, TestResult] = PostTestCase(test).lift

}
