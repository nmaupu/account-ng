package controllers

import java.util.{GregorianCalendar, Date}

import models.model._
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
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
  implicit val userWrites = Json.writes[User]
  implicit val categoryWrites = Json.writes[Category]
  /*implicit val writesExpenseCategoryUser: Writes[ExpenseFull] = (
    (__ \ 'expense \ 'amount).write[Double] and
    (__ \ 'expense \ 'date).write[Date] and
    (__ \ 'expense \ 'comment).write[String] and
    (__ \ 'category \ 'name).lazyWrite(Writes.traversableWrites[Category](categoryWrites)) and
    (__ \ 'user \ 'login).lazyWrite(Writes.traversableWrites[User](userWrites))
  )(unlift(ExpenseFull.unapply))*/

  def expenses = Action {
    val fromDate = new GregorianCalendar(2010, 1, 1)
    val endDate = new GregorianCalendar
    Ok(Json.toJson(Expense.all()))
  }

  def addExpense = Action(parse.json) { request =>
    request.body.validate[(Double, Date, Long)].map {
      case (amount, date, category) => Ok(Json.toJson(Expense.insert(Expense(None, amount, date, "", category, -1))))
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
