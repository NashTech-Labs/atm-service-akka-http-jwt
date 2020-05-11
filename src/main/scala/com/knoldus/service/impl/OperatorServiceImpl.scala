package com.knoldus.service.impl

import akka.http.scaladsl.server.Directives._
import com.knoldus.service.OperatorService
import com.knoldus.service.handler.OperatorServiceHandler
import com.knoldus.util.JsonSupport
import akka.http.scaladsl.server.Route
import com.knoldus.model.{Operator, OperatorInput, Users}
import com.knoldus.persistence.{ATMDbConnection, OperatorDbOperations}
import com.knoldus.util.Constants.{ATM, OPERATOR, USERS}

/**
 * Create User: POST - /atm/operator/create/users :: List[Users]
 * Create Operator: POST - /atm/operator/create/operator :: List[Operator]
 * Deposit Money: PUT - /atm/operator/deposit :: OperatorInput
 * Check Balance: GET - /atm/operator/balance
 */

class OperatorServiceImpl(operatorServiceHandler: OperatorServiceHandler) extends OperatorService with JsonSupport {

  // POST - /atm/operator/create/users :: List[Users]
  override def createUser: Route =
    pathPrefix(ATM / OPERATOR) {
      post {
        path("create" / USERS) {
          entity(as[List[Users]]) { user =>
            operatorServiceHandler.createUser(user)
          }
        }
      }
    }

  // POST - /atm/operator/create/operator :: List[Operator]
  override def createOperator: Route =
    pathPrefix(ATM / OPERATOR) {
      post {
        path("create" / OPERATOR) {
          entity(as[List[Operator]]) { operator =>
            operatorServiceHandler.createOperator(operator)
          }
        }
      }
    }

  // PUT - /atm/operator/deposit :: OperatorInput
  override def depositMoney: Route =
    pathPrefix(ATM / OPERATOR) {
      put {
        path("deposit") {
          entity(as[OperatorInput]) { operator =>
            operatorServiceHandler.depositMoney(operator)
          }
        }
      }
    }

  // GET - /atm/operator/balance
  override def checkBalance: Route =
    pathPrefix(ATM / OPERATOR) {
      get {
        path("balance") {
          pathEndOrSingleSlash {
            operatorServiceHandler.checkBalance()
          }
        }
      }
    }
}

object OperatorServiceImpl {
  def apply(): OperatorServiceImpl = {
    val dbOps = new OperatorDbOperations(ATMDbConnection)
    val operatorServiceHandler = new OperatorServiceHandler(dbOps)
    new OperatorServiceImpl(operatorServiceHandler)
  }

  val operator = apply()
  lazy val operatorRoutes: Route = operator.createUser ~ operator.createOperator ~ operator.depositMoney ~ operator.checkBalance
}
