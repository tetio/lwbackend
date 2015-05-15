package com.buzzfactory.lw


import java.util.Calendar

import org.json4s.ShortTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization._
import scala.slick.driver.PostgresDriver.simple._
import java.sql.{Date, Timestamp}

import scala.util.Random

/**
 * Created by tetio on 30/04/15.
 */
object GameHandler {

  val db = Database.forURL("jdbc:postgresql://localhost:5432/lw", "lw", "lwbackend", driver = "org.postgresql.Driver")

  private implicit val formats = Serialization.formats( ShortTypeHints(List(classOf[Games], classOf[Players], classOf[Rounds], classOf[UsedWords])))

  val games: TableQuery[Games] = TableQuery[Games]
  val players: TableQuery[Players] = TableQuery[Players]
  val rounds: TableQuery[Rounds] = TableQuery[Rounds]
  val usedWords: TableQuery[UsedWords] = TableQuery[UsedWords]
  val words16: TableQuery[Words16] = TableQuery[Words16]

  def createDB = 1 //(games.ddl ++ players.ddl ++ rounds.ddl ++ usedWords.ddl).create

  def newGame(username: String, numPlayers: Int, language: String) = {
    var auxGame: Game = null
    db.withTransaction { implicit session =>
      if (numPlayers == 1) {
        auxGame = createGame(username, numPlayers, language, "READY")
      } else {
        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.MINUTE, -2)
        val twoMinutesAgo = new Timestamp(cal.getTimeInMillis())
        val gameQuery = games.filter(_.state === "OPEN").filter(_.doc > twoMinutesAgo).sortBy(_.doc)
        val listOfPendingGames = gameQuery.take(1).run
        if (!listOfPendingGames.isEmpty) {
          auxGame = listOfPendingGames.head
          games.filter(_.id === auxGame.id).map(_.state).update("BLOCKED")
        } else {
          auxGame = createGame(username, numPlayers, language, "OPEN")
        }
      }
      val player = Player(None, username, auxGame.id.get)
      players += player
      val listPlayers = players.filter(_.gameId === auxGame.id.get).run
      val game = Game(auxGame.id, auxGame.doc, auxGame.numPlayers, auxGame.language, auxGame.state, "", auxGame.letters, listPlayers.map(_.username), Nil)
      writePretty(game)
    }
  }

  def createGame(username: String, numPlayers: Int, language: String, state: String)(implicit session: Session ) = {
      val countQuery = words16.length.run
      //      val countQuery = 874
      val word16Idx = Random.nextInt(countQuery)
      val word = words16.drop(word16Idx).take(1).run.head.word
      val shuffledWord = Random.shuffle(word.toList).mkString
      val g = Game(None, new Timestamp(System.currentTimeMillis()), numPlayers, language, state, shuffledWord, Nil,  Nil, Nil)
      val gameId = (games returning games.map(_.id))  += g
      Game(Some(gameId), g.doc, g.numPlayers, g.language, g.state, "", shuffledWord.map(_.toString), Nil, Nil)
  }


  def findAllGames(): String = {
    db.withSession { implicit session =>
      val gameQuery: Query[Games, Game, Seq] = games.filter(_.id > 0)
      val gameList = gameQuery.list
      writePretty(gameList)
    }
  }

  def findById(id: Int): String = {
    db.withSession { implicit session =>
      val gameQuery = games.filter(_.id === id)
      val usedWordsQuery: Query[UsedWords, UsedWord, Seq] = usedWords.filter(_.gameId === id)
      val myGame = gameQuery.run.head
      val megaGame = Game(myGame.id, myGame.doc, myGame.numPlayers, myGame.language, myGame.state, "", Random.shuffle(myGame.word.split("")), Nil, usedWordsQuery.run.map(_.word))
      writePretty(megaGame )
    }
  }


  def findById2(id: Int): String = {
    db.withSession { implicit session =>
      val usedWordsQuery: Query[UsedWords, UsedWord, Seq] = usedWords.filter(_.gameId > 0)
      val gamesQuery: Query[Games, Game, Seq] = usedWordsQuery.flatMap(_.game)
      val games: Seq[Game] = gamesQuery.run
      writePretty(games)
    }
  }

  def findAll2(): String = {
    db.withSession { implicit session =>
      val joinQuery: Query[(Column[Int], Column[String]), (Int, String), Seq] = for {
        uw <- usedWords if uw.word.length > 3
        g <- uw.game if g.id > 1
      } yield (g.id, uw.word)
      writePretty(joinQuery.list.map(e => Pair(e._1, e._2)))
    }
  }
}
