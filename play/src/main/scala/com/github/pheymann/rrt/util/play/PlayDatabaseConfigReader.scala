package com.github.pheymann.rrt.util.play

import com.github.pheymann.rrt.io.DbService.DatabaseType
import com.github.pheymann.rrt.{DatabaseConfig, TestConfig}
import com.typesafe.config.ConfigFactory

trait PlayDatabaseConfigReader {

  def readConfig(dbType: DatabaseType, database: String = "default"): DatabaseConfig = {
    val configReader = ConfigFactory.load.getConfig(s"db.$database")

    DatabaseConfig(
      dbType,
      configReader.getString("driver"),
      configReader.getString("url"),
      configReader.getString("username"),
      configReader.getString("password")
    )
  }

  implicit class TestConfigExtension(config: TestConfig) {

    def withDatabase(dbType: DatabaseType, database: String = "default"): TestConfig = {
      config.copy(dbConfigOpt = Some(readConfig(dbType, database)))
    }

  }

}

object PlayDatabaseConfigReader extends PlayDatabaseConfigReader
