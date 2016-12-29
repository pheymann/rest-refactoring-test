package com.github.pheymann.rrt

import cats.free.Free

sealed trait TestAction[R]

object TestAction {

  /** A random value generator will create a value of type `A`
    * on call.
    *
    * @tparam A
    */
  type RandomValueGen[A] = () => A

  implicit class TestActionLifter[R](action: TestAction[R]) {

    /** Lifts the `TestAction` into a `Free`.
      *
      * @return action lifted into `Free`
      */
    def lift: Free[TestAction, R] = Free.liftF(action)

  }

  /** Creates a `RandomValueGen` for a `List` of static values. The generator will
    * randomly select an element from the `List` on call.
    *
    * @param data static `List` of values
    * @tparam A
    */
  final case class StaticData[A](data: List[A]) extends TestAction[RandomValueGen[A]]

  /** Creates a `RandomValueGen` for `Int` values.
    *
    * @param maxOpt optional upper/lower bound of the values
    */
  final case class IntData(maxOpt: Option[Int] = None) extends TestAction[RandomValueGen[Int]]

  /** Creates a `RandomValueGen` for positive `Int` values (> 0).
    *
    * @param maxOpt optional upper bound of the values
    */
  final case class PositiveIntData(maxOpt: Option[Int] = None) extends TestAction[RandomValueGen[Int]]

  /** Creates a `RandomValueGen` for `Long` values.
    *
    * @param maxOpt optional upper/lower bound of the values
    */
  final case class LongData(maxOpt: Option[Long] = None) extends TestAction[RandomValueGen[Long]]

  /** Creates a `RandomValueGen` for `Double` values.
    *
    * @param maxOpt optional upper/lower bound of the values
    */
  final case class DoubleData(maxOpt: Option[Double] = None) extends TestAction[RandomValueGen[Double]]

  sealed trait DbReadyTestAction[R]

  final case class FromDatabase[R](
                                    table:     String,
                                    pkColumn:  String,
                                    resultColumn: String,
                                    action: DbReadyTestAction[R]
                                  ) extends TestAction[R]

  implicit class WithFromDatabase[R](action: DbReadyTestAction[R]) {

    /** Wrappes the database action into a `FromDatabase` which stores all
      * necessary data to execute the query.
      *
      * @param table target table
      * @param selectCol column holding some `Number` which is used to randomly select the rows
      * @param resultCol column holding the requested data
      * @return `DbReadyTestAction` wrapped in `FromDatabase`
      */
    def from(table:     String,
             selectCol: String,
             resultCol: String): Free[TestAction, R] = FromDatabase(table, selectCol, resultCol, action).lift

  }

  /** Read `Int` values from database.
    *
    * @param size at most `size` values
    */
  final case class RetrieveInts(size: Int) extends DbReadyTestAction[RandomValueGen[Int]]

  /** Read `Long` values from database.
    *
    * @param size at most `size` values
    */
  final case class RetrieveLongs(size: Int) extends DbReadyTestAction[RandomValueGen[Long]]

  /** Read `Double` values from database.
    *
    * @param size at most `size` values
    */
  final case class RetrieveDoubles(size: Int) extends DbReadyTestAction[RandomValueGen[Double]]

  /** Read `String` values from database.
    *
    * @param size at most `size` values
    */
  final case class RetrieveStrings(size: Int) extends DbReadyTestAction[RandomValueGen[String]]

  /** Run the test case against a GET endpoint.
    *
    * @param test request generator
    */
  final case class GetTestCase(test: EndpointTestCase) extends TestAction[TestResult]

  /** Run the test case against a POST endpoint.
    *
    * @param test request generator
    */
  final case class PostTestCase(test: EndpointTestCase) extends TestAction[TestResult]

  /** Run the test case against a PUT endpoint.
    *
    * @param test request generator
    */
  final case class PutTestCase(test: EndpointTestCase) extends TestAction[TestResult]

  /** Run the test case against a DELETE endpoint.
    *
    * @param test request generator
    */
  final case class DeleteTestCase(test: EndpointTestCase) extends TestAction[TestResult]

}

trait TestActionSyntax {

  import TestAction._

  /** Creates a `RandomValueGen` for a `List` of static values. The generator will
    * randomly select an element from the `List` on call.
    *
    * @param values static `List` of values
    * @tparam A
    */
  def staticData[A](values: A*): Free[TestAction, RandomValueGen[A]] = StaticData(values.toList).lift

  /** Creates a `RandomValueGen` for `Int` values.
    *
    * @param maxOpt optional upper/lower bound of the values
    */
  def intData(maxOpt: Option[Int] = None): Free[TestAction, RandomValueGen[Int]] = IntData(maxOpt).lift

  /** Creates a `RandomValueGen` for positive `Int` values.
    *
    * @param maxOpt optional upper bound of the values
    */
  def positiveIntData(maxOpt: Option[Int] = None): Free[TestAction, RandomValueGen[Int]] = PositiveIntData(maxOpt).lift

  /** Creates a `RandomValueGen` for `Long` values.
    *
    * @param maxOpt optional upper/lower bound of the values
    */
  def longData(maxOpt: Option[Long] = None): Free[TestAction, RandomValueGen[Long]] = LongData(maxOpt).lift

  /** Creates a `RandomValueGen` for `Double` values.
    *
    * @param maxOpt optional upper/lower bound of the values
    */
  def doubleData(maxOpt: Option[Double] = None): Free[TestAction, RandomValueGen[Double]] = DoubleData(maxOpt).lift

  /** Read `Int` values from database.
    *
    * @param size at most `size` values
    */
  def retrieveInts(size: Int): RetrieveInts = RetrieveInts(size)

  /** Read `Long` values from database.
    *
    * @param size at most `size` values
    */
  def retrieveLongs(size: Int): RetrieveLongs = RetrieveLongs(size)

  /** Read `Double` values from database.
    *
    * @param size at most `size` values
    */
  def retrieveDoubles(size: Int): RetrieveDoubles = RetrieveDoubles(size)

  /** Read `String` values from database.
    *
    * @param size at most `size` values
    */
  def retrieveStrings(size: Int): RetrieveStrings = RetrieveStrings(size)

  /** Run the test case against a GET endpoint.
    *
    * @param test request generator
    */
  def testGet(test: EndpointTestCase): Free[TestAction, TestResult] = GetTestCase(test).lift

  /** Run the test case against a POST endpoint.
    *
    * @param test request generator
    */
  def testPost(test: EndpointTestCase): Free[TestAction, TestResult] = PostTestCase(test).lift

  /** Run the test case against a PUT endpoint.
    *
    * @param test request generator
    */
  def testPut(test: EndpointTestCase): Free[TestAction, TestResult] = PutTestCase(test).lift

  /** Run the test case against a DELETE endpoint.
    *
    * @param test request generator
    */
  def testDelete(test: EndpointTestCase): Free[TestAction, TestResult] = DeleteTestCase(test).lift

}

object TestActionSyntax extends TestActionSyntax
