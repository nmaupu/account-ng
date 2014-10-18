package controllers

import java.text.SimpleDateFormat
import java.util.{Locale, GregorianCalendar, Date}

import models.{ExpenseFull, Category, User, Expense}
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
    (__ \ 'comment).read[String] and
    (__ \ 'categoryId).read[Long] and
    (__ \ 'userId).read[Long]
  ) tupled
  implicit val rdsId = (__ \ 'id).read[Long]
  implicit val rdsDates = (
    (__ \ 'from).read[String] and
    (__ \ 'to).read[String]
  ) tupled

  /**
   * Json writes
   */
  implicit val expenseWrites = Json.writes[Expense]
  implicit val userWrites = Json.writes[User]
  implicit val categoryWrites = Json.writes[Category]
  implicit val expenseFullWrites = Json.writes[ExpenseFull]

  def expenses(from: String, to: String) = Action { implicit request =>
    val sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault)
    Ok(Json.toJson(Expense.list(sdf.parse(from), sdf.parse(to))))
  }

  def addExpense = Action(parse.json) { request =>
    request.body.validate[(Double, Date, String, Long, Long)].map {
      case (amount, date, comment, categoryId, userId) => Ok(Json.toJson(Expense.insert(Expense(None, amount, date, comment, categoryId, userId))))
    }.recoverTotal {
      e => BadRequest("Detected error: " + JsError.toFlatJson(e))
    }
  }

  def deleteExpense = Action(parse.json) { implicit request =>
    request.body.validate[Long].map {
      case id => Ok(Json.toJson(Expense.delete(id) == 1))
    }.recoverTotal {
      e => BadRequest("Detected error: " + JsError.toFlatJson(e))
    }
  }
}
