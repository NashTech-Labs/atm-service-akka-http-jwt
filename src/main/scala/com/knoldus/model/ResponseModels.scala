package com.knoldus.model

import com.knoldus.util.Constants._
import spray.json._

trait ApiResponse

object ApiResponse {
  implicit val responseJW: JsonWriter[ApiResponse] = new JsonWriter[ApiResponse] {
    override def write(obj: ApiResponse): JsValue = obj match {
      case standardErrorResponse: StandardErrorResponse => standardErrorResponse.toJson
      case standardResponse: SuccessResponse => standardResponse.toJson
      case postRouteResponse: SuccessResponseWithMessage => postRouteResponse.toJson
    }
  }
}

object SuccessResponse extends DefaultJsonProtocol {
  implicit val standardRespJW: JsonWriter[SuccessResponse] = (obj: SuccessResponse) => {
    val fields = Array(
      Some((STATUS, JsString(obj.status))),
      Some((USER_LIST, obj.metadataList.toJson))).flatten

    JsObject(fields: _*)
  }

  implicit val standardRespJW1: JsonWriter[Map[String, Any]] = (obj: Map[String, Any]) => {
    val fields: Array[(String, JsValue)] = obj.map {
      case (k, v) => Some((k, anyJW2.write(v)))
    }.toArray.flatten
    JsObject(fields: _*)
  }

  implicit val standardRespJW2: JsonWriter[List[Map[String, Any]]] = (obj: List[Map[String, Any]]) => {
    val fields: List[JsValue] = obj.map { _.toJson }
    JsArray(fields)
  }

  implicit val anyJW2: JsonWriter[Any] = (obj: Any) => {
    obj match {
      case i: Int => JsNumber(i)
      case s: String => JsString(s)
      case b: Boolean => JsBoolean(b)
      case bi: BigInt => JsNumber(bi)
      case d: Double => JsNumber(d)
      case f: Float => JsNumber(f)
      case si: Short => JsNumber(si)
      case lg: Long => JsNumber(lg)
      case byte: Byte => JsNumber(byte)
      case _  =>  JsNull
    }
  }
}

final case class SuccessResponse(status: String, metadataList: List[Map[String, Any]]) extends ApiResponse

object SuccessResponseWithMessage extends DefaultJsonProtocol {
  implicit val standardRespJW: JsonWriter[SuccessResponseWithMessage] = (obj: SuccessResponseWithMessage) => {
    val fields = Array(
      Some((STATUS, JsString(obj.status))),
      Some((MESSAGE, obj.message.toJson))).flatten

    JsObject(fields: _*)
  }
}

final case class SuccessResponseWithMessage(status: String, message: String) extends ApiResponse

object Error extends DefaultJsonProtocol {
  implicit val ErrorFormat: RootJsonFormat[Error] = jsonFormat2(Error.apply)
}

final case class Error(id: Option[String] = None, message: Option[String] = None)

object StandardErrorResponse extends DefaultJsonProtocol {
  implicit val standardRespJW: JsonWriter[StandardErrorResponse] = (obj: StandardErrorResponse) => {
    val fields = Array(
      Some((STATUS, JsString(obj.status))),
      Some((ERROR, obj.error.toJson))).flatten

    JsObject(fields: _*)
  }
}

final case class StandardErrorResponse(status: String, error: Error) extends ApiResponse
