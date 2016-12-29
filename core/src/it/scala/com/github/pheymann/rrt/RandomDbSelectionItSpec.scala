package com.github.pheymann.rrt

import com.github.pheymann.rrt.TestAction.{GetTestCase, RetrieveInts, RetrieveStrings}
import com.github.pheymann.rrt.io.DbService
import com.github.pheymann.rrt.io.DbService.MySQL
import org.specs2.mutable.Specification
import scalikejdbc._

class RandomDbSelectionItSpec extends Specification {

  sequential

  val testName = "random-db-selection-it-spec"
  val testConfig = newConfig(testName, "127.0.0.1", 10000, "127.0.0.1", 10001)
    .withRepetitions(10)
    .withDatabase(MySQL, "org.h2.Driver", "jdbc:h2:mem:test", "user", "pass")

  DbService.newDriver(testConfig.dbConfigOpt.get)

  DB.autoCommit { implicit session =>
    sql"""
      create table user_names (
        id bigint not null primary key,
        name varchar(64)
      )
    """.execute.apply()

    List("Luke", "Boba", "Yoda", "Anakin", "Han", "C3PO", "R2D2", "ObiWan", "Padme", "Leia").zipWithIndex.foreach { case (name, id) =>
      sql"insert into user_names (id, name) values ($id, $name)".update.apply()
    }
  }

  "The library api" should {
    "provide an action to select randomly `String` values from a table in a database" in new WithTestServices(testConfig) {
      val testCase = for {
        names   <- RetrieveStrings(3).fromDb("user_names", "id", "name")
        result  <- GetTestCase { _ =>
          val uri = s"/hello/${names()}"

          |+|(uri)
        }.lift
      } yield result

      checkAndLog(testCase.runCase(testConfig)) should beTrue
    }

    "provide an action to select randomly `Int` values from a table in a database" in new WithTestServices(testConfig) {
      val testCase = for {
        a <- RetrieveInts(3).fromDb("user_names", "id", "id")
        b <- RetrieveInts(3).fromDb("user_names", "id", "id")
        result  <- GetTestCase { _ =>
          val uri = s"/add/${a()}/and/${b()}"

          |+|(uri)
        }.lift
      } yield result

      checkAndLog(testCase.runCase(testConfig)) should beTrue
    }
  }

}
