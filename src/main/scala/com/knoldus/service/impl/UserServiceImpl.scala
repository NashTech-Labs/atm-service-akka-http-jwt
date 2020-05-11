package com.knoldus.service.impl

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import com.knoldus.service.UserService
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.knoldus.model.{UserInput, UserPin, Users}
import com.knoldus.persistence.ATMDbConnection
import com.knoldus.service.handler.UserServiceHandler
import com.knoldus.util.Constants.{ATM, USERS}
import com.knoldus.persistence.UserDbOperations
import com.knoldus.util.JsonSupport

/**
 * Create User: POST - /atm/users/create :: List[Users]
 * Check Balance: GET - /atm/users/check/{debitCardID}
 * Withdraw Money: PUT - /atm/users/withdraw :: UserInput
 * Show User: GET - /amt/users/show/{debitCardID}
 * Deposit Money: PUT - /atm/users/deposit :: UserInput
 * Pin Change: PUT - /atm/users/changepin :: UserPin
 */

class UserServiceImpl(userServiceHandler: UserServiceHandler) extends UserService with JsonSupport {

  import com.knoldus.util.JwtAuthorization._

  // GET - /atm/users/check/{debitCardID}
  override def checkBalance: Route =
    pathPrefix(ATM / USERS) {
      get {
        path("check" / LongNumber) { debitCardID =>
          optionalHeaderValueByName("Authorization") {
            case Some(token) =>
              if(isTokenValid(token, debitCardID)) {
                if(isTokenExpired(token, debitCardID)) {
                  complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "Token Expired."))
                } else {
                  userServiceHandler.checkBalance(debitCardID)
                }
              } else {
                complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "Token is invalid, or has been tampered."))
              }
            case _ => complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "No token provided."))
          }
        }
      }
    }

  // PUT - /atm/users/withdraw :: UserInput
  override def withdrawMoney: Route =
    pathPrefix(ATM / USERS) {
      put {
        path("withdraw") {
          entity(as[UserInput]) { user =>
            optionalHeaderValueByName("Authorization") {
              case Some(token) =>
                if(isTokenValid(token, user.debitCardID)) {
                  if(isTokenExpired(token, user.debitCardID)) {
                    complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "Token Expired."))
                  } else {
                    userServiceHandler.withdrawMoney(user)
                  }
                } else {
                  complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "Token is invalid, or has been tampered."))
                }
              case _ => complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "No token provided."))
            }
          }
        }
      }
    }
  // PUT - /atm/users/deposit :: UserInput
  override def userDepositMoney: Route =
    pathPrefix(ATM / USERS) {
      put {
        path("deposit") {
          entity(as[UserInput]) { user =>
            optionalHeaderValueByName("Authorization") {
              case Some(token) =>
                if(isTokenValid(token, user.debitCardID)) {
                  if(isTokenExpired(token, user.debitCardID)) {
                    complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "Token Expired."))
                  } else {
                    userServiceHandler.depositMoney(user)
                  }
                } else {
                  complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "Token is invalid, or has been tampered."))
                }
              case _ => complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "No token provided."))
            }
          }
        }
      }
    }

  // PUT - /atm/users/changepin :: UserPin
  override def changePinCode: Route =
    pathPrefix(ATM / USERS) {
      put {
        path("changepin") {
          entity(as[UserPin]) { user =>
            optionalHeaderValueByName("Authorization") {
              case Some(token) =>
                if(isTokenValid(token, user.debitCardID)) {
                  if(isTokenExpired(token, user.debitCardID)) {
                    complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "Token Expired."))
                  } else {
                    userServiceHandler.changePinCode(user)
                  }
                } else {
                  complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "Token is invalid, or has been tampered."))
                }
              case _ => complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "No token provided."))
            }
          }
        }
      }
    }
}

object UserServiceImpl {
  def apply(): UserServiceImpl = {
    val dbOps = new UserDbOperations(ATMDbConnection)
    val userServiceHandler = new UserServiceHandler(dbOps)
    new UserServiceImpl(userServiceHandler)
  }

  val user = apply()
  lazy val userRoutes: Route = user.checkBalance ~ user.withdrawMoney ~ user.userDepositMoney ~ user.changePinCode
}