package com.knoldus.service

import akka.http.scaladsl.server.Route

trait OperatorService {
  def createUser: Route
  def createOperator: Route
  def depositMoney: Route
  def checkBalance: Route
}
