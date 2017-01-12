package com.github.pheymann.rrt.util.play

import com.github.pheymann.rrt.DatabaseConfig
import com.github.pheymann.rrt.io.DbService.MySQL
import org.specs2.mutable.Specification

class PlayDatabaseConfigReaderSpec extends Specification {

  "The PlayDatabaseConfigReader" should {
    "read Play database configs and create a `DatabaseConfig` instance" in {
      readConfig(MySQL, "test") should beEqualTo(DatabaseConfig(
        MySQL,
        "com.mysql.jdbc.Driver",
        "jdbc:mysql://localhost/test",
        "testuser",
        "testpassword"
      ))
    }
  }

}
