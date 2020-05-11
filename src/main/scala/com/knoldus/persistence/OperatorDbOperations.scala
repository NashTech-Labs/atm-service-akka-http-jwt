package com.knoldus.persistence

import java.sql.{Connection, ResultSet, Statement}

import com.knoldus.model.{ATM, Operator, OperatorInput, Users}
import com.knoldus.util.Constants.{EMPTY_STRING, INVALID, NOT_FOUND}
import com.knoldus.util.Logging

class OperatorDbOperations(dbConn: DbConnection) extends Logging {

  private val conn: Connection = dbConn.getDbConnection

  private val statement: Statement = conn.createStatement()

  def createUserInDB(users: List[Users]): (Int, String) = {
    val sqlStatement: Statement = this.statement
    try {
      users.foreach { user =>
        val insertQuery = CrudHelper.makeInsertUserQuery(user)
        sqlStatement.addBatch(insertQuery)
      }
      val insertedRows = sqlStatement.executeBatch()
      (insertedRows.length, EMPTY_STRING)
    } catch {
      case e: Exception =>
        (0, e.getMessage)
    }
  }

  def createOperatorInDB(operators: List[Operator]): (Int, String) = {
    val sqlStatement: Statement = this.statement
    try {
      operators.foreach { operator =>
        val insertQuery = CrudHelper.makeInsertOperatorQuery(operator)
        println(insertQuery)
        sqlStatement.addBatch(insertQuery)
      }
      val insertedRows = sqlStatement.executeBatch()
      (insertedRows.length, EMPTY_STRING)
    } catch {
      case e: Exception =>
        (0, e.getMessage)
    }
  }

  def depositMoneyToATM(operator: OperatorInput): (ATM, String) = {
    val sqlStatement: Statement = this.statement
    val query = "select * from atm"

    try {
      val resultSet: ResultSet = sqlStatement.executeQuery(query)

      val (atmInfo, flag) = if(resultSet.next()) {
        (ATM(resultSet.getString(1), resultSet.getString(2), resultSet.getLong(3)), true)
      } else {
        (ATM(EMPTY_STRING, EMPTY_STRING, 0), false)
      }

      if(flag) {
        //Check if operator is valid
        val validOperatorQuery = CrudHelper.isValidOperatorQuery(operator.operID)
        val rs: ResultSet = sqlStatement.executeQuery(validOperatorQuery)
        val isValidOperator = if(rs.next()) {
          if (rs.getInt(1) == 1) true else false
        } else false

        if(isValidOperator) {
          val totBalance = atmInfo.balance + operator.depositAmount
          val atmBalanceQuery = CrudHelper.updateAtmBalance(totBalance)
          sqlStatement.executeUpdate(atmBalanceQuery)

          (ATM(atmInfo.atmID, atmInfo.location, totBalance), EMPTY_STRING)
        } else {
          (atmInfo, INVALID)  //If operator ID is invalid
        }
      } else (atmInfo, NOT_FOUND)

    } catch {
      case e: Exception =>
        error(s"Found error: ${e.getMessage}")
        (ATM(EMPTY_STRING, EMPTY_STRING, 0), e.getMessage)
    }
  }

  def checkBalanceOfATM(): (ATM, String) = {
    val sqlStatement: Statement = this.statement
    val query = "select * from atm"

    try {
      val resultSet: ResultSet = sqlStatement.executeQuery(query)

      if (resultSet.next()) {
        (ATM(resultSet.getString(1), resultSet.getString(2), resultSet.getLong(3)), EMPTY_STRING)
      } else {
        (ATM(EMPTY_STRING, EMPTY_STRING, 0), NOT_FOUND)
      }
    } catch {
      case e: Exception =>
        error(s"Found error: ${e.getMessage}")
        (ATM(EMPTY_STRING, EMPTY_STRING, 0), e.getMessage)
    }
  }
}
