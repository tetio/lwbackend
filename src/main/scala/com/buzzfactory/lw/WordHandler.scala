package com.buzzfactory.lw

import org.json4s.ShortTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization._

import scala.slick.lifted.TableQuery
import scala.slick.driver.PostgresDriver.simple._

/**
 * Created by tetio on 13/05/15.
 */
object WordHandler {
  val db = Database.forURL("jdbc:postgresql://localhost:5432/lw", "lw", "lwbackend", driver = "org.postgresql.Driver")
  val word: TableQuery[Words] = TableQuery[Words]
  val word16: TableQuery[Words16] = TableQuery[Words16]
  private implicit val formats = Serialization.formats( ShortTypeHints(List(classOf[Words], classOf[Words16])))

  def validate(aWord: String) = {
    db.withSession { implicit session =>
      val query = word.filter(_.word === aWord.toUpperCase)
      val result = !(query.run.isEmpty)
      s"{result: $result}"
    }
  }

  def findWord(aWord: String) = {
    db.withSession { implicit session =>
      val query = word.filter(_.word === aWord.toUpperCase)
      val theWord = query.run
      writePretty(theWord)
    }
  }

}
