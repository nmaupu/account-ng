package controllers

import java.util.Date

import controllers.CategoryController._
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import models.Expense
import play.api.libs.json._
// you need this import to have combinators
import play.api.libs.functional.syntax._


/**
 * Created by nmaupu on 12/10/14.
 */
object ExpenseController extends Controller {
  /**
   * Json reads
   */
  implicit val rdsExpense = (
  (__ \ 'amount).read[Double] and
    (__ \ 'date).read[Date] and
    (__ \ 'category).read[Long]
  ) tupled
  implicit val rdsId = (__ \ 'id).read[Long]

  /**
   * Json writes
   */
  implicit val expenseWrites = Json.writes[Expense]

  def expenses = Action {
    Ok(Json.toJson(Expense.all))
  }
  
  def addExpense = Action(parse.json) { request =>
    request.body.validate[(Double, Date, Long)].map {
      case (amount, date, category) => Ok(Json.toJson(Expense.create(amount, date, category) == 1))
    }.recoverTotal {
      e => BadRequest("Detected error: " + JsError.toFlatJson(e))
    }
  }

  def deleteExpense = Action(parse.json) { implicit request =>
    request.body.validate[Long].map {
      case id => Ok(Json.toJson(Expense.delete(id) == 1))
    }.recoverTotal {
      e => BadRequest("Detected error: " + JsError.toFlatForm(e))
    }
  }
}
