package controllers

import models.Category
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import play.api.libs.json._


/**
 * Created by nmaupu on 12/10/14.
 */
object CategoryController extends Controller {
  /**
   * Json reads
   */
  implicit val rdsName = (__ \ 'name).read[String]
  implicit val rdsId = (__ \ 'id).read[Long]

  /**
   * Json writes
   */
  implicit val categoryWrites = Json.writes[Category]

  def categories = Action {
    Ok(Json.toJson(Category.list()))
  }

  def addCategory = Action(parse.json) { implicit request =>
    request.body.validate[String].map {
      case name => Ok(Json.toJson(Category.insert(Category(None, name))))
    }.recoverTotal {
      e => BadRequest("Detected error: " + JsError.toFlatJson(e))
    }
  }

  def deleteCategory() = Action(parse.json) { implicit request =>
    request.body.validate[Long].map {
      case id => Ok(Json.toJson(Category.delete(id)))
    }.recoverTotal {
      e => BadRequest("Detected error: " + JsError.toFlatJson(e))
    }
  }
}
