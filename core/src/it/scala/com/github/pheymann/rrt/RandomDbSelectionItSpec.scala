package com.github.pheymann.rrt

import com.github.pheymann.rrt.TestAction.{GetTestCase, RetrieveInts, RetrieveStrings}
import com.github.pheymann.rrt.io.DbService
import com.github.pheymann.rrt.io.DbService.MySQL
import org.specs2.mutable.Specification
import scalikejdbc._

import scala.util.Random

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
        score double,
        name varchar(64)
      )
    """.execute.apply()

    val rand = new Random(System.currentTimeMillis)

    List("Luke", "Boba", "Yoda", "Anakin", "Han", "C3PO", "R2D2", "ObiWan", "Padme", "Leia").zipWithIndex.foreach { case (name, id) =>
      sql"insert into user_names (id, score, name) values ($id, ${rand.nextDouble()}, $name)".update.apply()
    }
  }

  "The library api" should {
    "provide an action to select randomly `String` values from a table in a database" in new WithTestServices(testConfig) {
      val testCase = for {
        names   <- retrieveStrings(3).from("user_names", "id", "name")
        result  <- testGet { _ =>
          s"/hello/${names()}"
        }
      } yield result

      checkAndLog(testCase.runSeq(testConfig)) should beTrue
    }

    "provide an action to select randomly `Int` values from a table in a database" in new WithTestServices(testConfig) {
      val testCase = for {
        a <- retrieveInts(3).from("user_names", "id", "id")
        b <- retrieveInts(3).from("user_names", "id", "id")
        result  <- testGet { _ =>
          s"/add/${a()}/and/${b()}"
        }
      } yield result

      checkAndLog(testCase.runSeq(testConfig)) should beTrue
    }

    "provide an action to select randomly `Long` values from a table in a database" in new WithTestServices(testConfig) {
      val testCase = for {
        a <- retrieveLongs(3).from("user_names", "id", "id")
        b <- retrieveLongs(3).from("user_names", "id", "id")
        result  <- testGet { _ =>
          s"/add/${a()}/and/${b()}"
        }
      } yield result

      checkAndLog(testCase.runSeq(testConfig)) should beTrue
    }

    "provide an action to select randomly `Double` values from a table in a database" in new WithTestServices(testConfig) {
      val testCase = for {
        a <- retrieveDoubles(3).from("user_names", "id", "score")
        b <- retrieveDoubles(3).from("user_names", "id", "score")
        result  <- testGet { _ =>
          s"/multiply/${a()}/and/${b()}"
        }
      } yield result

      checkAndLog(testCase.runSeq(testConfig)) should beTrue
    }
  }

}
