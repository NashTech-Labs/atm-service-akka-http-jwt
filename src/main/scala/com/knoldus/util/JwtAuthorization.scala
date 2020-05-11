package com.knoldus.util

import java.util.concurrent.TimeUnit

import com.knoldus.persistence.UserDbOperations
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtSprayJson}

import scala.util.{Failure, Success}

object JwtAuthorization {

  private val algorithm = JwtAlgorithm.HS256
  private val secretKey = "kn0ldu$"

  def checkPinCode(debitCardId: Long, pinCode: Int): Boolean = UserDbOperations.checkPinCodeInDB(debitCardId, pinCode)

  def createToken(debitCardId: Long, expirationPeriodInMinutes: Int): String = {
    val claims = JwtClaim(
      expiration = Some(System.currentTimeMillis() / 1000 + TimeUnit.MINUTES.toSeconds(expirationPeriodInMinutes)),
      issuedAt = Some(System.currentTimeMillis() / 1000),
      issuer = Some("knoldus.com")
    )
    val key: String = debitCardId + secretKey
    JwtSprayJson.encode(claims, key, algorithm)
  }

  def isTokenExpired(token: String, debitCardId: Long): Boolean =
    JwtSprayJson.decode(token, debitCardId + secretKey, Seq(algorithm)) match {
      case Success(claims) => claims.expiration.getOrElse(0L) < System.currentTimeMillis() / 1000
      case Failure(_) => true
    }

  def isTokenValid(token: String, debitCardId: Long): Boolean =
    JwtSprayJson.isValid(token, debitCardId + secretKey, Seq(algorithm))

}
