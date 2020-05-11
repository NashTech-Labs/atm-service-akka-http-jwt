package com.knoldus.persistence

import java.sql.Connection

import com.knoldus.util.Logging
import com.knoldus.util.Constants.MYSQL_DRIVER
import org.apache.commons.dbcp2.BasicDataSource
import com.typesafe.config.ConfigFactory

object ATMDbConnection extends DbConnection with Logging {
  private val conf = ConfigFactory.load.getConfig("db")

  private lazy val host = conf.getString("mysql.hostname")
  private lazy val port = conf.getString("mysql.port_no")
  private lazy val dbName = conf.getString("mysql.dbname")
  private lazy val url = s"jdbc:mysql://$host:$port/$dbName?useSSL=false"
  private lazy val username = conf.getString("mysql.username")
  private lazy val password = conf.getString("mysql.password")

  private lazy val connectionPool: BasicDataSource = {
    val connectionPool = new BasicDataSource()
    connectionPool.setUsername(username)
    connectionPool.setPassword(password)
    connectionPool.setDriverClassName(MYSQL_DRIVER)
    connectionPool.setUrl(url)
    connectionPool.setInitialSize(5)
    connectionPool
  }

  override def getDbConnection: Connection = connectionPool.getConnection

  sys.addShutdownHook {
    connectionPool.close()
    info("Connection Pool is closing now...")
  }
}
