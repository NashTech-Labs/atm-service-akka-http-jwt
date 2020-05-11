package com.knoldus.persistence

import java.sql.Connection

trait DbConnection {

  def getDbConnection: Connection

}
