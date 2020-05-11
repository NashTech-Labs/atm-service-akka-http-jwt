package com.knoldus.service.handler

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import com.knoldus.model.{Operator, OperatorInput, Users}
import com.knoldus.persistence.OperatorDbOperations
import com.knoldus.util.Constants.{EMPTY_STRING, FAILED, INVALID, NOT_FOUND, SUCCESS}
import com.knoldus.util.{Logging, ResponseUtil}
import spray.json._

class OperatorServiceHandler (dbOps: OperatorDbOperations) extends ResponseUtil with Logging with SprayJsonSupport {

  def createUser(users: List[Users]): Route = {
    info("createUser invoked...")
    dbOps.createUserInDB(users) match {
      case (_, EMPTY_STRING) =>
        val message = s"Successfully created User with names [${users.map(col => {col.name}).mkString(", ")}]"
        info(message)
        complete(StatusCodes.OK, generateRouteResponseWithMsg(SUCCESS, message).toJson)
      case (_, errorMsg) =>
        error(s"Found error: $errorMsg")
        complete(StatusCodes.BadRequest, sendFormattedError(FAILED, errorMsg).toJson)
    }
  }

  def createOperator(operators: List[Operator]): Route = {
    info("createOperator invoked...")
    dbOps.createOperatorInDB(operators) match {
      case (_, EMPTY_STRING) =>
        val message = s"Successfully created Operator with IDs [${operators.map(col => {col.operID}).mkString(", ")}]"
        info(message)
        complete(StatusCodes.OK, generateRouteResponseWithMsg(SUCCESS, message).toJson)
      case (_, errorMsg) =>
        error(s"Found error: $errorMsg")
        complete(StatusCodes.BadRequest, sendFormattedError(FAILED, errorMsg).toJson)
    }
  }

  def depositMoney(operator: OperatorInput): Route = {
    info(s"depositMoney invoked by Operator - ${operator.operID}...")
    if (operator.depositAmount < 100) {
      val message = "Cannot deposit less then Rs. 100."
      info(message)
      complete(StatusCodes.BadRequest, sendFormattedError(FAILED, message).toJson)
    } else if (operator.depositAmount % 100 != 0) {
      val message = "Deposit amount should be in multiple of 100's."
      info(message)
      complete(StatusCodes.BadRequest, sendFormattedError(FAILED, message).toJson)
    } else {
      dbOps.depositMoneyToATM(operator) match {
        case (_, NOT_FOUND) =>
          val message = s"No record found in ATM table. Insert one!"
          info(message)
          complete(StatusCodes.NotFound, sendFormattedError(FAILED, message).toJson)
        case (_, INVALID) =>
          val message = s"No such Operator with ID - ${operator.operID} found!!"
          info(message)
          complete(StatusCodes.NotFound, sendFormattedError(FAILED, message).toJson)
        case (atm, EMPTY_STRING) =>
          val message = s"Deposit of Rs. ${operator.depositAmount} by Operator ${operator.operID} is successful. " +
            s"Available balance in ATM [${atm.atmID}] is: Rs. ${atm.balance}"
          info(message)
          complete(StatusCodes.OK, generateRouteResponseWithMsg(SUCCESS, message).toJson)
        case (_, errorMsg) =>
          error(s"Found error: $errorMsg")
          complete(StatusCodes.BadRequest, sendFormattedError(FAILED, errorMsg).toJson)
      }
    }
  }

  def checkBalance(): Route = {
    dbOps.checkBalanceOfATM() match {
      case (_, NOT_FOUND) =>
        val message = s"No record found in ATM table. Insert one!"
        info(message)
        complete(StatusCodes.NotFound, sendFormattedError(FAILED, message).toJson)
      case (atm, EMPTY_STRING) =>
        val message = s"ATM with ID - ${atm.atmID} situated at ${atm.location} has available balance - Rs. ${atm.balance}"
        info(message)
        complete(StatusCodes.OK, generateRouteResponseWithMsg(SUCCESS, message).toJson)
      case (_, errorMsg) =>
        error(s"Found error: $errorMsg")
        complete(StatusCodes.BadRequest, sendFormattedError(FAILED, errorMsg).toJson)
    }
  }

}
