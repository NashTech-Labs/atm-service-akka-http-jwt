package com.knoldus.service

import akka.http.scaladsl.server.Route

trait UserService {
  def checkBalance: Route
  def withdrawMoney: Route
  def userDepositMoney: Route
  def changePinCode: Route
}
