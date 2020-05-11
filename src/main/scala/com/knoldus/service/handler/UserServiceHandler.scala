package com.knoldus.service.handler

import com.knoldus.model.{UserInput, UserPin, Users}
import com.knoldus.persistence.UserDbOperations
import com.knoldus.util.{Logging, ResponseUtil}
import com.knoldus.util.Constants.{EMPTY_STRING, ERROR, FAILED, NOT_FOUND, SUCCESS}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import spray.json._

class UserServiceHandler(dbOps: UserDbOperations) extends ResponseUtil with Logging with SprayJsonSupport {

  def checkBalance(debitCardID: Long): Route = {
    info(s"checkBalance invoked by $debitCardID...")
    dbOps.checkBalanceInDB(debitCardID) match {
      case (modelUser, EMPTY_STRING) =>
        val message = s"Hi, ${modelUser.name}, your balance against Card No. ${modelUser.debitCardID} is ${modelUser.balance}"
        info(message)
        complete(StatusCodes.OK, generateRouteResponseWithMsg(SUCCESS, message).toJson)
      case (modelUser, NOT_FOUND) =>
        val message = s"No such User found with Card No. ${modelUser.debitCardID}!!"
        info(message)
        complete(StatusCodes.NotFound, sendFormattedError(FAILED, message).toJson)
      case (_, errorMsg) =>
        error(s"Found error: $errorMsg")
        complete(StatusCodes.BadRequest, sendFormattedError(FAILED, errorMsg).toJson)
    }
  }

  def withdrawMoney(user: UserInput): Route = {
    info(s"withdrawMoney invoked by ${user.debitCardID}...")
    if(user.amount < 100) {
      val message = "Cannot withdraw less then Rs. 100."
      info(message)
      complete(StatusCodes.BadRequest, sendFormattedError(FAILED, message).toJson)
    } else if(user.amount > 10000) {
      val message = "You cannot withdraw more then Rs. 10,000 in one transaction."
      info(message)
      complete(StatusCodes.BadRequest, sendFormattedError(FAILED, message).toJson)
    } else if(user.amount % 100 != 0) {
      val message = "Withdraw money should be in multiple of 100's."
      info(message)
      complete(StatusCodes.BadRequest, sendFormattedError(FAILED, message).toJson)
    } else {
      dbOps.withdrawMoneyFromAccount(user) match {
        case (debitCardId, NOT_FOUND) =>
          val message = s"No such User found with Card No. $debitCardId!!"
          info(message)
          complete(StatusCodes.NotFound, sendFormattedError(FAILED, message).toJson)
        case (EMPTY_STRING, status) =>
          info(status)
          complete(StatusCodes.BadRequest, sendFormattedError(FAILED, status).toJson)
        case (message, EMPTY_STRING) =>
          info(message)
          complete(StatusCodes.OK, generateRouteResponseWithMsg(SUCCESS, message).toJson)
        case (ERROR, errorMsg) =>
          error(s"Found error: $errorMsg")
          complete(StatusCodes.BadRequest, sendFormattedError(FAILED, errorMsg).toJson)
      }
    }
  }

  def depositMoney(user: UserInput): Route = {
    info(s"depositMoney invoked by User - ${user.debitCardID}...")
    if (user.amount < 100) {
      val message = "Cannot deposit less then Rs. 100."
      info(message)
      complete(StatusCodes.BadRequest, sendFormattedError(FAILED, message).toJson)
    } else if (user.amount % 100 != 0) {
      val message = "Deposit amount should be in multiple of 100's."
      info(message)
      complete(StatusCodes.BadRequest, sendFormattedError(FAILED, message).toJson)
    } else {
      dbOps.depositMoneyToAccount(user) match {
        case (debitCardId, NOT_FOUND) =>
          val message = s"No such User found with Card No. $debitCardId!!"
          info(message)
          complete(StatusCodes.NotFound, sendFormattedError(FAILED, message).toJson)
        case (message, EMPTY_STRING) =>
          info(message)
          complete(StatusCodes.OK, generateRouteResponseWithMsg(SUCCESS, message).toJson)
        case (ERROR, errorMsg) =>
          error(s"Found error: $errorMsg")
          complete(StatusCodes.BadRequest, sendFormattedError(FAILED, errorMsg).toJson)
      }
    }
  }

  def changePinCode(user: UserPin): Route = {
    info(s"changePinCode invoked by ${user.debitCardID}...")
    if(!(user.newPin >= 1000 && user.newPin <= 9999)) {
      val message = "Pin should be of 4 digit!!"
      info(message)
      complete(StatusCodes.BadRequest, sendFormattedError(FAILED, message).toJson)
    } else {
      dbOps.changePinCodeOfUser(user) match {
        case (debitCardId, NOT_FOUND) =>
          val message = s"No such User found with Card No. $debitCardId!!"
          info(message)
          complete(StatusCodes.NotFound, sendFormattedError(FAILED, message).toJson)
        case (EMPTY_STRING, status) =>
          info(status)
          complete(StatusCodes.BadRequest, sendFormattedError(FAILED, status).toJson)
        case (message, EMPTY_STRING) =>
          info(message)
          complete(StatusCodes.OK, generateRouteResponseWithMsg(SUCCESS, message).toJson)
        case (ERROR, errorMsg) =>
          error(s"Found error: $errorMsg")
          complete(StatusCodes.BadRequest, sendFormattedError(FAILED, errorMsg).toJson)
      }
    }
  }
}
