package com.github.pheymann.rrt

import com.github.pheymann.rrt.io.DbService.DatabaseType

import scala.concurrent.duration._

final case class TestConfig(
                             name: String,
                             actual: ServiceConfig,
                             expected: ServiceConfig,

                             headers: List[(String, String)] = Nil,
                             bodyRemovals: List[String] = Nil,
                             showDiffs: Boolean = false,

                             dbConfigOpt: Option[DatabaseConfig] = None,

                             repetitions: Int = 1,
                             requestPerSecondOpt: Option[Int] = None,
                             timeout: FiniteDuration = 21400000.seconds
                           ) {

  /** Adds standard headers to all requests.
    *
    * @param headers `List` of headers
    * @return updated config
    */
  def withHeaders(headers: List[(String, String)]): TestConfig = this.copy(headers = headers)

  /** Adds a regex pattern which removes elements from the response body (entity). This can
    * be useful if the response contains some values which differ for two service instances.
    *
    * @param removals regex pattern
    * @return updated config
    */
  def withBodyRemovals(removals: List[String]): TestConfig = this.copy(bodyRemovals = removals)

  /** Show differences of response json rather than the whole bodies.
    *
    * @param show
    * @return
    */
  def showJsonDiffs(show: Boolean): TestConfig = this.copy(showDiffs = show)

  /** Adds a database connection.
    *
    * @param dbType which database is used
    * @param driver database driver
    * @param url database url containing the schema
    * @param user user name
    * @param password user password
    * @return updated config
    */
  def withDatabase(dbType: DatabaseType, driver: String, url: String, user: String, password: String): TestConfig = {
    this.copy(dbConfigOpt = Some(DatabaseConfig(dbType, driver, url, user, password)))
  }

  /** Sets the number of repetitions to a new value (default 1).
    *
    * @param repetitions number of repetitions
    * @return updated config
    */
  def withRepetitions(repetitions: Int): TestConfig = this.copy(repetitions = repetitions)

  /** Sets the maximum number of requests per second.
    *
    * @param requestPerSecond maximum number of requests per second
    * @return updated config
    */
  def withThrottling(requestPerSecond: Int): TestConfig = this.copy(requestPerSecondOpt = Some(requestPerSecond))

  /** Sets timeout to a new value (default `Long.MaxValue` nanoseconds).
    *
    * @param timeout REST call timeout
    * @return updated config
    */
  def setTimeout(timeout: FiniteDuration): TestConfig = this.copy(timeout = timeout)

}

final case class ServiceConfig(host: String, port: Int)

final case class DatabaseConfig(dbType: DatabaseType, driver: String, url: String, user: String, password: String)
