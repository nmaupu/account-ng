package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._

/**
 * Created by nmaupu on 11/10/14.
 */
case class Category (id: Long, name: String)
  object Category {
    val categorySQLExtractor = {
      get[Long]("id") ~ get[String]("name") map {
        case id~name => Category(id, name)
      }
    }

    def all(): List[Category] = DB.withConnection { implicit c =>
      SQL("select * from category").as(categorySQLExtractor *)
    }

    def retrieve(id: Long): Category = DB.withConnection { implicit c =>
      SQL("select * from category where id={id}")
        .on('id -> id).as(categorySQLExtractor *)
        .head
    }

    def create(name: String): Int = {
      DB.withConnection { implicit c =>
        return SQL("insert into category (name) values ({name})").
          on('name -> name).executeUpdate()
      }
    }

    def delete(id: Long): Int = {
      DB.withConnection { implicit  c =>
        return SQL("delete from category where id={id}").
          on('id -> id).executeUpdate()
      }
    }
  }
