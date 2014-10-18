package models

import java.util.Date
import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current


/**
 * Created by nmaupu on 13/10/14.
 */

case class User(id: Option[Long] = None, login: String, password: String)
case class Category(id: Option[Long] = None, name: String)
case class Expense(id: Option[Long] = None, amount: Double, date: Date, comment: String, categoryId: Long, userId: Long)
case class ExpenseFull(expense: Expense, category: Category, user: User)

object Expense {
  //-- Parsers
  
  /**
   * Parse an Expense from a ResultSet
   */
  val simpleParser = {
    get[Option[Long]]("expense.id") ~
    get[Double]("expense.amount") ~
    get[Date]("expense.date") ~
    get[String]("expense.comment") ~
    get[Long]("expense.categoryId") ~
    get[Long]("expense.userId") map {
      case id~amount~date~comment~categoryId~userId => Expense(id, amount, date, comment, categoryId, userId)
    }
  }

  val withCategoryUserParser = (Expense.simpleParser ~ Category.simpleParser ~ User.simpleParser) map {
    case expense~category~user => ExpenseFull(expense, category, user)
  }
  
  // -- Queries

  def all(): Seq[Expense] = {
    DB.withConnection { implicit connection =>
      SQL(
      """
        SELECT * from Expense
      """).as(Expense.simpleParser *)
    }
  }

  def list(fromDate: Date, toDate: Date): Seq[ExpenseFull] = {
    DB.withConnection { implicit connection =>
      SQL(
      """
        SELECT e.*, c.*, u.* FROM expense e
        LEFT JOIN category c ON c.id = e.categoryId
        LEFT JOIN user u ON u.id = e.userId
        WHERE e.date > {fromDate} AND e.date < {toDate}
        ORDER BY e.date DESC
      """).on('fromDate -> fromDate, 'toDate -> toDate)
        .as(withCategoryUserParser *)
    }
  }

  /**
   * Retrieve an Expense from id
   * @param id The expense id
   * @return Expense from DB
   */
  def findById(id: Long): Option[Expense] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM expense where id = {id}")
        .on('id -> id)
        .as(Expense.simpleParser.singleOpt)
    }
  }

  /**
   * Update an Expense
   * @param expense The updated Expense
   * @return true if update is successful, false otherwise
   */
  def update(id: Long, expense: Expense): Boolean = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          UPDATE expense
          SET amount = {amount}, date = {date}, comment = {comment}, categoryId = {categoryId}, userId = {userId}
          WHERE id = {id}
        """)
        .on(
          'id -> id,
          'amount -> expense.amount,
          'date -> expense.date,
          'comment -> expense.comment,
          'categoryId -> expense.categoryId,
          'userId -> expense.userId)
        .executeUpdate() == 1
    }
  }

  /**
   * Insert an expense into DB
   * @param expense The expense to insert
   * @return True if insertion is successful, false otherwise
   */
  def insert(expense: Expense): Boolean = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          INSERT INTO expense (amount, date, comment, categoryId, userId) VALUES (
            {amount}, {date}, {comment}, {categoryId}, {userId}
          )
        """)
        .on('amount -> expense.amount,
            'date -> expense.date,
            'comment -> expense.comment,
            'categoryId -> expense.categoryId,
            'userId -> expense.userId)
        .executeUpdate() == 1
    }
  }

  def delete(id: Long): Boolean = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          DELETE FROM expense
          WHERE id = {id}
        """)
        .on('id -> id)
        .executeUpdate() == 1
    }
  }
}

object Category {
  /**
   * Parse a Category from a ResultSet
   */
  val simpleParser = {
    get[Option[Long]]("category.id") ~
    get[String]("category.name") map {
      case id~name => Category(id, name)
    }
  }

  /**
   * Get all category from DB
   * @return A list of Category
   */
  def list(): List[Category] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * from category")
        .as(Category.simpleParser *)
    }
  }

  /**
   * Update a Category in DB
   * @param id The id of the Category to update
   * @param category The new Category
   * @return True if update is successful, false otherwise
   */
  def update(id:Long, category: Category): Boolean = {
    DB.withConnection { implicit connection =>
      SQL(
      """
        UPDATE category
        SET name = {name}
        WHERE id = {id}
      """)
        .on('id -> id, 'name -> category.name)
        .executeUpdate() == 1
    }
  }

  /**
   * Insert a new Category in DB
   * @param category The new Category to insert
   * @return True if insert is successful, false otherwise
   */
  def insert(category: Category): Boolean = {
    DB.withConnection { implicit connection =>
      SQL(
      """
        INSERT INTO category (name) VALUES
        (
          {name}
        )
      """)
        .on('name -> category.name)
        .executeUpdate() == 1
    }
  }

  /**
   * Delete a Category from DB
   * @param id The id corresponding to Category to delete
   * @return True if deletion is successful, false otherwise
   */
  def delete(id: Long): Boolean = {
    DB.withConnection { implicit connection =>
      SQL(
      """
        DELETE FROM category
        WHERE id = {id}
      """)
        .on('id -> id)
        .executeUpdate() == 1
    }
  }
}

object User {
  /**
   * Parse a User from a ResultSet
   */
  val simpleParser = {
    get[Option[Long]]("user.id") ~
    get[String]("user.login") ~
    get[String]("user.password") map {
      case id~login~password => User(id, login, password)
    }
  }

  /**
   * List all users
   * @return A list of all users
   */
  def list(): Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * from user")
        .as(User.simpleParser *)
    }
  }

  /**
   * Insert a new user in DB
   * @param user The user to insert
   * @return True inserted successfully, false otherwise
   */
  def insert(user: User): Boolean = {
    DB.withConnection { implicit connection =>
      SQL(
        """
        INSERT INTO user (login, password) VALUES
        (
          {login}, {password}
        )
        """)
        .on('login -> user.login, 'password -> user.password)
        .executeUpdate() == 1
    }
  }

  /**
   * Delete a user from DB
   * @param id The id of user to delete
   * @return True if deleted successfully, false otherwise
   */
  def delete(id: Long): Boolean = {
    DB.withConnection { implicit connection =>
      SQL(
        """
        DELETE FROM user
        WHERE id = {id}
        """)
        .on('id -> id)
        .executeUpdate() == 1
    }
  }

  /**
   * Udate a user
   * @param id The id of user to update
   * @param user The new user information
   * @return True if updated successfully, false otherwise
   */
  def update(id:Long, user: User): Boolean = {
    DB.withConnection { implicit connection =>
      SQL(
        """
        UPDATE user
        SET login = {login}, password = {password}
        WHERE id = {id}
        """)
        .on('id -> id, 'login -> user.login, 'password -> user.password)
        .executeUpdate() == 1
    }
  }
}