package com.github.pheymann.rrt.io

import com.github.pheymann.rrt.DatabaseConfig
import scalikejdbc._

object DbService {

  def newDriver(config: DatabaseConfig): Unit = {
    Class.forName(config.driver)
    ConnectionPool.singleton(config.url, config.user, config.password)
  }

  private def selectRandom[A](table: String,
                              selectCol: String,
                              resultCol: String,
                              numOfResults: Int,
                              dbType: DatabaseType)
                             (mapper: WrappedResultSet => A): List[A] = DB.readOnly { implicit session =>
    dbType.maxSelectColumnValueQuery(table, selectCol) match {
      case Some(maxValue) => dbType.randomSelectWithGabsQuery(table, selectCol, resultCol, numOfResults, maxValue, mapper)
      case None => throw new NoSuchElementException(s"no maximum value found for $table[$selectCol, $resultCol]")
    }
  }

  def selectRandomInts(table: String,
                       selectCol: String,
                       resultCol: String,
                       numOfResults: Int,
                       dbType: DatabaseType): List[Int] = {
    selectRandom(table, selectCol, resultCol, numOfResults, dbType)(_.int(1))
  }

  def selectRandomLongs(table: String,
                        selectCol: String,
                        resultCol: String,
                        numOfResults: Int,
                        dbType: DatabaseType): List[Long] = {
    selectRandom(table, selectCol, resultCol, numOfResults, dbType)(_.long(1))
  }

  def selectRandomDoubles(table: String,
                          selectCol: String,
                          resultCol: String,
                          numOfResults: Int,
                          dbType: DatabaseType): List[Double] = {
    selectRandom(table, selectCol, resultCol, numOfResults, dbType)(_.double(1))
  }

  def selectRandomStrings(table: String,
                          selectCol: String,
                          resultCol: String,
                          numOfResults: Int,
                          dbType: DatabaseType): List[String] = {
    selectRandom(table, selectCol, resultCol, numOfResults, dbType)(_.string(1))
  }

  final case class UnsupportedDatabaseType(msg: String) extends Exception(msg)
  case object UndefinedDatabase extends Exception

  sealed trait DatabaseType {

    private[io] def maxSelectColumnValueQuery(table: String, selectCol: String)
                                             (implicit session: DBSession): Option[Long] = {
      val query = StringContext(
        """
          |SELECT MAX(%s)
          |FROM %s
        """.stripMargin.format(selectCol, table))

      query.sql().map(_.long(1)).single().apply()
    }

    private[io] def randomSelectWithGabsQuery[A](table: String,
                                                 selectCol: String,
                                                 resultCol: String,
                                                 limit: Int,
                                                 maxSelectValue: Long,
                                                 mapper: WrappedResultSet => A)
                                                (implicit session: DBSession): List[A]

  }

  object MySQL extends DatabaseType {

    private[io] def randomSelectWithGabsQuery[A](table: String,
                                                 selectCol: String,
                                                 resultCol: String,
                                                 limit: Int,
                                                 maxSelectValue: Long,
                                                 mapper: WrappedResultSet => A)
                                                (implicit session: DBSession): List[A] = {
      val query = new StringContext(
        """
          |SELECT DISTINCT t.%s
          |FROM %s AS t
          |  JOIN (
          |    SELECT id
          |    FROM (
          |      SELECT %s AS id
          |      FROM (
          |        SELECT %d * RAND() AS start
          |        FROM DUAL
          |      ) AS init
          |       JOIN %s y
          |      WHERE y.%s > init.start
          |      ORDER BY y.%s
          |      LIMIT %d
          |    ) z
          |    ORDER BY RAND()
          |    LIMIT %d
          |  ) r ON t.%s = r.id
        """.stripMargin.format(
          resultCol,
          table,
          selectCol,
          maxSelectValue,
          table,
          selectCol,
          selectCol,
          limit * 100,
          limit,
          selectCol
        )
      )

      query.sql().map(mapper).list().apply()
    }

  }

}
