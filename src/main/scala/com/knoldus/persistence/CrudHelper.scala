package com.knoldus.persistence

import com.knoldus.model.{Operator, UserPin, Users}

object CrudHelper {

  def makeInsertUserQuery(user: Users): String = "insert into users values ('" + user.userId + "', '" + user.name + "', " +
    user.debitCardID + ", " + user.pin + ", " + user.balance + ")"

  def makeSelectQuery(debitCardID: Long): String = "select name, balance from users where debitcardid = " + debitCardID

  def updateUserBalance(debitCardID: Long, amount: Double): String = "update users set balance = " + amount + " where debitcardid = " + debitCardID

  def updateAtmBalance(balance: Long): String = "update atm set balance = " + balance

  def selectPinCode(debitCardID: Long): String = "select pin from users where debitcardid = " + debitCardID

  def updatePin(user: UserPin): String = "update users set pin = " + user.newPin + " where debitcardid = " + user.debitCardID

  def makeInsertOperatorQuery(oper: Operator): String = "insert into operator values ('" + oper.operID + "', '" + oper.password + "')"

  def isValidOperatorQuery(operID: String): String = "select count(*) from operator where operid = '" + operID + "'"

  def isValidPinCode(debitCardID: Long): String = "select pin from users where debitcardid = " + debitCardID

}
