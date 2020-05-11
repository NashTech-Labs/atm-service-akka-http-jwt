package com.knoldus.util

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.knoldus.model.{LoginRequest, Operator, OperatorInput, StandardErrorResponse, UserInput, UserPin, Users}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val userJsonFormat: RootJsonFormat[Users] = jsonFormat5(Users.apply)
  implicit val userInputJsonFormat: RootJsonFormat[UserInput] = jsonFormat2(UserInput.apply)
  implicit val userPinJsonFormat: RootJsonFormat[UserPin] = jsonFormat3(UserPin.apply)
  implicit val operatorJsonFormat: RootJsonFormat[Operator] = jsonFormat2(Operator.apply)
  implicit val operatorInputJsonFormat: RootJsonFormat[OperatorInput] = jsonFormat2(OperatorInput.apply)
  implicit val loginRequestFormat: RootJsonFormat[LoginRequest] = jsonFormat2(LoginRequest.apply)
  implicit val errorResponseFormat: RootJsonFormat[StandardErrorResponse] = jsonFormat2(StandardErrorResponse.apply)
}
