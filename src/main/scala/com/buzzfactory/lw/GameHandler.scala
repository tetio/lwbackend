package com.buzzfactory.lw

import java.sql.Date

import org.json4s.ShortTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization._

import scala.slick.driver.PostgresDriver.simple._

/**
 * Created by tetio on 30/04/15.
 */
object GameHandler {

  val db = Database.forURL("jdbc:postgresql://localhost:5432/lw", "lw", "lwbackend", driver = "org.postgresql.Driver")
  implicit val session = connect()

  private implicit val formats = Serialization.formats( ShortTypeHints(List(classOf[Games], classOf[Players], classOf[Rounds], classOf[UsedWords])))
  val game: TableQuery[Games] = TableQuery[Games]
  val player: TableQuery[Players] = TableQuery[Players]
  val round: TableQuery[Rounds] = TableQuery[Rounds]
  val usedWord: TableQuery[UsedWords] = TableQuery[UsedWords]

  def initDB = 1 //(games.ddl ++ players.ddl ++ rounds.ddl ++ usedWords.ddl).create

  def connect() = {
    db.createSession()
  }

  def findById(id: Int): String = {
    val games: Query[Games, Game, Seq] = game.filter(_.id > 0)
    return writePretty(games.list)
  }

  def findUsedWords(id: Int): String = {
    val usedWordsQuery: Query[UsedWords, UsedWord, Seq] = usedWord.filter(_.gameId === id)
    return writePretty(usedWordsQuery.run)
  }


  def findById2(id: Int): String = {

    val usedWordsQuery: Query[UsedWords, UsedWord, Seq] = usedWord.filter(_.gameId > 0)
    val gamesQuery: Query[Games, Game, Seq] = usedWordsQuery.flatMap(_.game)
    val games: Seq[Game] = gamesQuery.run

    return writePretty(games)
  }
}
