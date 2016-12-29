package com.github.pheymann.rrt

import cats.{Id, ~>}
import TestAction._
import akka.actor.ActorSystem
import com.github.pheymann.rrt.io.DbService
import com.github.pheymann.rrt.io.DbService.UndefinedDatabase
import com.github.pheymann.rrt.util.RandomUtil

object TestActionInterpreter {

  import RandomUtil._

  def interpreter(config: TestConfig)
                 (implicit system: ActorSystem): TestAction ~> Id = new (TestAction ~> Id) {

    config.dbConfigOpt.foreach(DbService.newDriver)

    override def apply[R](action: TestAction[R]): Id[R] = action match {
      case StaticData(data) => () => nextFromSeq(data)
      case IntData(maxOpt)  => () => maxOpt.fold(rand.nextInt())(rand.nextInt)
      case PositiveIntData(maxOpt) => () => maxOpt.fold(nextPositiveInt())(nextPositiveInt)
      case LongData(maxOpt)   => () => maxOpt.fold(rand.nextLong())(nextLong)
      case DoubleData(maxOpt) => () => maxOpt.fold(rand.nextDouble())(nextDouble)

      case GetTestCase(test)  => TestRunner.runGetSequential(test, config, RandomUtil)
      case PostTestCase(test) => TestRunner.runPostSequential(test, config, RandomUtil)
      case PutTestCase(test)  => TestRunner.runPutSequential(test, config, RandomUtil)
      case DeleteTestCase(test) => TestRunner.runDeleteSequential(test, config, RandomUtil)

      case FromDatabase(table, selectCol, resultCol, _action) =>
        config.dbConfigOpt.fold(throw UndefinedDatabase)(dbInterpreter(table, selectCol, resultCol, _action, _))
    }
  }

  import DbService._

  private def dbInterpreter[R](table: String,
                               pkCol: String,
                               resultCol: String,
                               action: DbReadyTestAction[R],
                               config: DatabaseConfig): Id[R] = action match {
    case RetrieveInts(size) =>
      val intValues = selectRandomInts(table, pkCol, resultCol, size, config.dbType)

      () => nextFromSeq(intValues)

    case RetrieveLongs(size) =>
      val longValues = selectRandomLongs(table, pkCol, resultCol, size, config.dbType)

      () => nextFromSeq(longValues)

    case RetrieveDoubles(size) =>
      val doubleValues = selectRandomDoubles(table, pkCol, resultCol, size, config.dbType)

      () => nextFromSeq(doubleValues)

    case RetrieveStrings(size) =>
      val stringValues = selectRandomStrings(table, pkCol, resultCol, size, config.dbType)

      () => nextFromSeq(stringValues)
  }

}
