package com.knoldus

import akka.http.scaladsl.Http
import com.knoldus.service.impl.{OperatorServiceImpl => operatorService, UserServiceImpl => userService}
import com.knoldus.util.{AkkaConfig, Logging}
import com.typesafe.config.ConfigFactory
import com.knoldus.util.Constants.{DEFAULT_HOST, DEFAULT_PORT}
import akka.http.scaladsl.server.Directives._
import com.knoldus.service.LoginService

import scala.util.{Failure, Success, Try}

object ApplicationRunner extends App with AkkaConfig with Logging {

  private val conf = ConfigFactory.load.getConfig("app")
  private lazy val hostName = Try(conf.getString("hostname")).toOption.getOrElse(DEFAULT_HOST)
  private lazy val port = Try(conf.getString("portno").toInt).toOption.getOrElse(DEFAULT_PORT)

  val routes = LoginService.loginRoute ~ userService.userRoutes ~ operatorService.operatorRoutes

  val bindingFuture = Http().bindAndHandle(routes, hostName, port)

  bindingFuture.onComplete {
    case Success(binding) =>
      val localAddress = binding.localAddress
      info(s"ATM Server is listening on ${localAddress.getHostName}:${localAddress.getPort} !!!")
    case Failure(exception) => error(s"Unable to start ATM due to - $exception")
  }
}
