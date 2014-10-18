package controllers

import models.User
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import play.api.libs.json._
// you need this import to have combinators
import play.api.libs.functional.syntax._

/**
 * Created by nmaupu on 12/10/14.
 */
object UserController extends Controller {
  /**
   * Json reads
   */
  implicit val rdsId = (__ \ 'id).read[Long]
  implicit val rdsUser = (
    (__ \ 'login).read[String] and
    (__ \ 'password).read[String]
  ) tupled


  /**
   * Json writes
   */
  implicit val userWrites = Json.writes[User]

  def users = Action {
    Ok(Json.toJson(User.list()))
  }

  def addUser = Action(parse.json) { implicit request =>
    request.body.validate[(String, String)].map {
      case (login, password) => Ok(Json.toJson(User.insert(User(None, login, password))))
    }.recoverTotal {
      e => BadRequest("Detected error: " + JsError.toFlatJson(e))
    }
  }

  def deleteUser() = Action(parse.json) { implicit request =>
    request.body.validate[Long].map {
      case id => Ok(Json.toJson(User.delete(id)))
    }.recoverTotal {
      e => BadRequest("Detected error: " + JsError.toFlatJson(e))
    }
  }
}
