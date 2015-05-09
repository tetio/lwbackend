package com.buzzfactory.lw

import java.sql.Timestamp

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted.{ProvenShape, ForeignKeyQuery}

/**
 * Created by tetio on 30/04/15.
 */

class Games(tag: Tag) extends Table[Game](tag, "games") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def doc = column[Timestamp]("doc", O.NotNull)
  def state = column[String]("state",O.NotNull)

  def mapRow = (id: Option[Int], doc: Timestamp, state: String) =>
    Game(id, doc, state, Nil, Nil, Nil)

  def unMapRow = (game: Game) =>
    Some((game.id, game.doc, game.state))

  def * = (id.?, doc, state) <> (mapRow.tupled, unMapRow)

}

class UsedWords(tag: Tag) extends Table[UsedWord](tag, "used_words") {
  def word = column[String]("id", O.PrimaryKey)
  def gameId = column[Int]("game_id")

  def * = (word, gameId) <> (UsedWord.tupled, UsedWord.unapply)

  def game: ForeignKeyQuery[Games, Game] =
    foreignKey("USE_GAM_FK", gameId, TableQuery[Games])(_.id)
}


class Players(tag: Tag) extends Table[Player](tag, "players") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def username = column[String]("username", O.NotNull)
  def gameId = column[Int]("game_id")

  def * = (id, username, gameId) <> (Player.tupled, Player.unapply)

  def game: ForeignKeyQuery[Games, Game] =
    foreignKey("PLA_GAM_FK", gameId, TableQuery[Games])(_.id)
}


class Rounds(tag: Tag) extends Table[Round](tag, "rounds") {
  def id = column[Int]("id", O.PrimaryKey)
  def word = column[String]("word", O.NotNull)
  def playerId = column[Int]("player_id")

  def * = (id, word, playerId) <> (Round.tupled, Round.unapply)

  def player: ForeignKeyQuery[Players, Player] =
    foreignKey("ROU_PLA_FK", playerId, TableQuery[Players])(_.id)
}


class Words(tag: Tag) extends Table[Word](tag, "words") {
  def word = column[String]("word", O.PrimaryKey)

  def * = (word) <> (Word, Word.unapply)
}

class Words16(tag: Tag) extends Table[Word](tag, "words16") {
  def word = column[String]("word", O.PrimaryKey)

  def * = (word) <> (Word, Word.unapply)
}