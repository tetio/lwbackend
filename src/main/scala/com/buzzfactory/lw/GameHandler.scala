package com.buzzfactory.lw



import org.json4s.ShortTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization._
import scala.slick.driver.PostgresDriver.simple._
import java.sql.Timestamp

import scala.util.Random

/**
 * Created by tetio on 30/04/15.
 */
object GameHandler {

  val db = Database.forURL("jdbc:postgresql://localhost:5432/lw", "lw", "lwbackend", driver = "org.postgresql.Driver")
  //implicit val session = connect()

  private implicit val formats = Serialization.formats( ShortTypeHints(List(classOf[Games], classOf[Players], classOf[Rounds], classOf[UsedWords])))
  val game: TableQuery[Games] = TableQuery[Games]
  val player: TableQuery[Players] = TableQuery[Players]
  val round: TableQuery[Rounds] = TableQuery[Rounds]
  val usedWord: TableQuery[UsedWords] = TableQuery[UsedWords]
  val word16: TableQuery[Words16] = TableQuery[Words16]

  def createDB = 1 //(games.ddl ++ players.ddl ++ rounds.ddl ++ usedWords.ddl).create

  def newGame() = {
    db.withSession { implicit session =>
      val countQuery = word16.length.run
//      val countQuery = 874
      val w16Idx = Random.nextInt(countQuery)
      val word = word16.drop(w16Idx).take(1).run.head.word
      val letters = word.split("")
      val shuffled = Random.shuffle(word.toList)
      val g = Game(None, new Timestamp(System.currentTimeMillis()), "OPEN", shuffled.mkString, Nil,  Nil, Nil)
      val gameId = (game returning game.map(_.id))  += g
      val theGame = Game(Some(gameId), g.doc, g.state, "", shuffled.map(e=>e.toString), Nil, Nil)
      writePretty(theGame)
    }
  }


  def findAllGames(): String = {
    db.withSession { implicit session =>
      val games: Query[Games, Game, Seq] = game.filter(_.id > 0)
      val list = games.list
      writePretty(list)
    }
  }

  def findById(id: Int): String = {
    db.withSession { implicit session =>
      val gameQuery = game.filter(_.id === id)
      val usedWordsQuery: Query[UsedWords, UsedWord, Seq] = usedWord.filter(_.gameId === id)
      val myGame = gameQuery.run.head
      val megaGame = Game(myGame.id, myGame.doc, myGame.state, "", Random.shuffle(myGame.word.split("")), Nil, usedWordsQuery.run.map(_.word))
      writePretty(megaGame )
    }
  }


  def findById2(id: Int): String = {
    db.withSession { implicit session =>
      val usedWordsQuery: Query[UsedWords, UsedWord, Seq] = usedWord.filter(_.gameId > 0)
      val gamesQuery: Query[Games, Game, Seq] = usedWordsQuery.flatMap(_.game)
      val games: Seq[Game] = gamesQuery.run
      writePretty(games)
    }
  }

  def findAll2(): String = {
    db.withSession { implicit session =>
      val joinQuery: Query[(Column[Int], Column[String]), (Int, String), Seq] = for {
        uw <- usedWord if uw.word.length > 3
        g <- uw.game if g.id > 1
      } yield (g.id, uw.word)
      writePretty(joinQuery.list.map(e => Pair(e._1, e._2)))
    }
  }
}
