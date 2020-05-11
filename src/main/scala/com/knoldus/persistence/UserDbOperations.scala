package com.knoldus.persistence

import java.sql.{Connection, ResultSet, Statement}

import com.knoldus.model.{ModelUser, UserInput, UserPin, Users}
import com.knoldus.util.Constants.{EMPTY_STRING, ERROR, NOT_FOUND}
import com.knoldus.util.Logging

class UserDbOperations(dbConn: DbConnection) extends Logging {

  private val conn: Connection = dbConn.getDbConnection

  private val statement: Statement = conn.createStatement()

  def checkBalanceInDB(debitCardID: Long): (ModelUser, String) = {
    val sqlStatement: Statement = this.statement
    val userInfoQuery = CrudHelper.makeSelectQuery(debitCardID)
    try {
      val resultSet: ResultSet = sqlStatement.executeQuery(userInfoQuery)
      val (modelUser, message) = if(resultSet.next()) {
        (ModelUser(debitCardID, resultSet.getString(1), resultSet.getDouble(2)), EMPTY_STRING)
      } else {
        (ModelUser(debitCardID, EMPTY_STRING, 0.0), NOT_FOUND)
      }
      (modelUser, message)
    } catch {
      case e: Exception =>
        error(s"Found error: ${e.getMessage}")
        (ModelUser(debitCardID, EMPTY_STRING, 0.0), e.getMessage)
    }
  }

  def withdrawMoneyFromAccount(user: UserInput): (String, String) = {
    val sqlStatement: Statement = this.statement
    val query = "select balance from atm"

    try {
      val resultSet: ResultSet = sqlStatement.executeQuery(query)

      val atm_balance = if(resultSet.next()) resultSet.getInt(1) else 0

      val (message, status) = if(user.amount > atm_balance) {
        (EMPTY_STRING, "Insufficient Cash Present in ATM")
      } else {
        val userInfoQuery = CrudHelper.makeSelectQuery(user.debitCardID)
        val rs: ResultSet = sqlStatement.executeQuery(userInfoQuery)

        val (acc_balance, accountExist) = if(rs.next()) (rs.getDouble(2), true) else (0.0, false)

        if(accountExist) {
          if(acc_balance >= user.amount) {
            val avail = acc_balance - user.amount
            val userBalanceQuery = CrudHelper.updateUserBalance(user.debitCardID, avail)
            sqlStatement.executeUpdate(userBalanceQuery)

            val atmBalanceQuery = CrudHelper.updateAtmBalance(atm_balance - user.amount)
            sqlStatement.executeUpdate(atmBalanceQuery)

            val msg = s"Withdrawn successfully Rs. ${user.amount}. Your available balance is: Rs. $avail"
            (msg, EMPTY_STRING)
          } else {
            (EMPTY_STRING, "Insufficient balance in your account.")
          }
        } else (user.debitCardID.toString, NOT_FOUND)
      }
      (message, status)
    } catch {
      case e: Exception =>
        error(s"Found error: ${e.getMessage}")
        (ERROR, e.getMessage)
    }
  }

  def depositMoneyToAccount(user: UserInput): (String, String) = {
    val sqlStatement: Statement = this.statement
    val userInfoQuery = CrudHelper.makeSelectQuery(user.debitCardID)
    try {
      val rs: ResultSet = sqlStatement.executeQuery(userInfoQuery)

      val (acc_balance, accountExist) = if(rs.next()) (rs.getDouble(2), true) else (0.0, false)

      val (message, status) = if(accountExist) {
        val totBalance = acc_balance + user.amount
        val userBalanceQuery = CrudHelper.updateUserBalance(user.debitCardID, totBalance)
        sqlStatement.executeUpdate(userBalanceQuery)

        val msg = s"Deposit of Rs. ${user.amount} is successful. Your available balance is: Rs. $totBalance"
        (msg, EMPTY_STRING)
      } else (user.debitCardID.toString, NOT_FOUND)

      (message, status)
    } catch {
      case e: Exception =>
        error(s"Found error: ${e.getMessage}")
        (ERROR, e.getMessage)
    }
  }

  def changePinCodeOfUser(user: UserPin): (String, String) = {
    val sqlStatement: Statement = this.statement
    val userPinQuery = CrudHelper.selectPinCode(user.debitCardID)

    try {
      val rs: ResultSet = sqlStatement.executeQuery(userPinQuery)

      val (pinCode, accountExist) = if(rs.next()) (rs.getInt(1), true) else (0, false)

      val (message, status) = if(accountExist) {
        if(pinCode != user.oldPin) {
          (EMPTY_STRING, "Old Pin does not match!!")
        } else {
          val updatePinQuery = CrudHelper.updatePin(user)
          sqlStatement.executeUpdate(updatePinQuery)

          ("Pin Changed Successfully!!", EMPTY_STRING)
        }
      } else (user.debitCardID.toString, NOT_FOUND)
      (message, status)
    } catch {
      case e: Exception =>
        error(s"Found error: ${e.getMessage}")
        (ERROR, e.getMessage)
    }
  }



}

object UserDbOperations {
  val userDbOps = new UserDbOperations(ATMDbConnection)

  def checkPinCodeInDB(debitCardId: Long, pinCode: Int): Boolean = {
    val sqlStatement = userDbOps.statement
    val userInfoQuery = CrudHelper.isValidPinCode(debitCardId)
    try {
      val resultSet: ResultSet = sqlStatement.executeQuery(userInfoQuery)
      if(resultSet.next()) {
        resultSet.getInt(1) == pinCode
      } else false
    } catch {
      case e: Exception =>
        error(s"Found error: ${e.getMessage}")
        false
    }
  }
}
