package com.knoldus.service

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.knoldus.model.LoginRequest
import com.knoldus.util.{JsonSupport, Logging, ResponseUtil}
import com.knoldus.util.JwtAuthorization._
import com.knoldus.util.Constants.{ATM, FAILED, SUCCESS, USERS}
import spray.json._

object LoginService extends JsonSupport with ResponseUtil with Logging {

  // POST - /atm/users/token/create :: LoginRequest
  val loginRoute: Route =
    pathPrefix(ATM / USERS) {
      post {
        path("token" / "create") {
          entity(as[LoginRequest]) {
            case LoginRequest(debitCardId, pinCode) if checkPinCode(debitCardId, pinCode) =>
              val token = createToken(debitCardId,3)
              respondWithHeader(RawHeader("Access-Token", token)) {
                val message = "Authentication Successful. Your access token is generated."
                info(message)
                complete(StatusCodes.OK, generateRouteResponseWithMsg(SUCCESS, message).toJson)
              }
            case _ =>
              val message = "Authentication Failure! Please try again."
              error(message)
              complete(StatusCodes.Unauthorized, sendFormattedError(FAILED, message).toJson)
          }
        }
      }
    }
}
