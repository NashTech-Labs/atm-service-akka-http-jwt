package com.knoldus.util

import com.knoldus.model.{ApiResponse, StandardErrorResponse, SuccessResponse, SuccessResponseWithMessage, Error}
import com.knoldus.util.Constants.FAILED

trait ResponseUtil {

  def generateResponse(status: String, data: List[Map[String, Any]]): ApiResponse =
    SuccessResponse(status, data)

  def generateRouteResponseWithMsg(status: String, message: String): ApiResponse =
    SuccessResponseWithMessage(status, message)

  private def generateErrorResponse(status: String, error: Error): ApiResponse = {
    StandardErrorResponse(status, error)
  }

  def sendFormattedError(errorCode: String, errorMessage: String): ApiResponse = {
    val error = Error(Some(errorCode), Some(errorMessage))
    generateErrorResponse(FAILED, error)
  }
}

object ResponseUtil extends ResponseUtil
