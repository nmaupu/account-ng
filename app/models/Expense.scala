package models

import java.util.Date

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import play.api.Play.current

/**
 * Created by nmaupu on 11/10/14.
 */
case class Expense (id: Long, amount: Double, date: Date, category: Long)
  object Expense {
    implicit val expenseSQLExtractor = {
      get[Long]("id") ~
      get[Double]("amount") ~
      get[Date]("date") ~
      get[Long]("category") map {
        case id~amount~date~category => Expense(id, amount, date, category)
      }
    }

    def all(): List[Expense] = DB.withConnection { implicit c =>
      SQL("select * from expense").as(expenseSQLExtractor *)
    }

    def create(amount: Double, date: Date, category: Long): Int = {
      DB.withConnection { implicit c =>
        SQL("insert into expense (amount, date, category) values ({amount}, {date}, {category})")
          .on('amount -> amount, 'date -> date, 'category -> category)
          .executeUpdate()
      }
    }

    def delete(id: Long): Int = {
      DB.withConnection { implicit c =>
        SQL("delete from expense where id={id}")
          .on('id -> id)
          .executeUpdate()
      }
    }
  }
