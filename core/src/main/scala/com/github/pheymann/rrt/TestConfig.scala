package com.github.pheymann.rrt

import com.github.pheymann.rrt.io.DbService.DatabaseType

import scala.concurrent.duration._

final case class TestConfig(
                             name: String,
                             actual: ServiceConfig,
                             expected: ServiceConfig,

                             headers: List[(String, String)] = Nil,
                             bodyRemovals: List[String] = Nil,

                             dbConfigOpt: Option[DatabaseConfig] = None,

                             repetitions: Int = 1,
                             timeout: FiniteDuration = 10.seconds
                           ) {

  def withHeaders(headers: List[(String, String)]): TestConfig = this.copy(headers = headers)
  def withBodyRemovals(removals: List[String]): TestConfig = this.copy(bodyRemovals = removals)
  def withDatabase(dbType: DatabaseType, driver: String, url: String, user: String, password: String): TestConfig = {
    this.copy(dbConfigOpt = Some(DatabaseConfig(dbType, driver, url, user, password)))
  }
  def withRepetitions(repetitions: Int): TestConfig = this.copy(repetitions = repetitions)
  def setTimeout(timeout: FiniteDuration): TestConfig = this.copy(timeout = timeout)

}

final case class ServiceConfig(host: String, port: Int)

final case class DatabaseConfig(dbType: DatabaseType, driver: String, url: String, user: String, password: String)
