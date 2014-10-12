package controllers

import play.api.libs.json._
import play.api.mvc._

object Application extends Controller {

  val application_version = 1.0

  def version = Action {
    Ok(Json.toJson(application_version))
  }
}
