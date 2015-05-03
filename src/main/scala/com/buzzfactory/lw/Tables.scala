package com.buzzfactory.lw

import java.sql.Timestamp

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted.{ProvenShape, ForeignKeyQuery}

/**
 * Created by tetio on 30/04/15.
 */

class Games(tag: Tag) extends Table[Game](tag, "GAMES") {
  def id = column[Int]("ID", O.PrimaryKey)
  def doc = column[Timestamp]("DOC", O.NotNull)
  def state = column[String]("STATE", O.NotNull)

  def * = (id, doc, state) <> (Game.tupled, Game.unapply)

}

class UsedWords(tag: Tag) extends Table[UsedWord](tag, "USED_WORDS") {
  def word = column[String]("ID", O.PrimaryKey)
  def gameId = column[Int]("GAME_ID")

  def * = (word, gameId) <> (UsedWord.tupled, UsedWord.unapply)

  def game: ForeignKeyQuery[Games, Game] =
    foreignKey("USE_GAM_FK", gameId, TableQuery[Games])(_.id)
}


class Players(tag: Tag) extends Table[Player](tag, "PLAYERS") {
  def id = column[Int]("ID", O.PrimaryKey)
  def username = column[String]("USERNAME", O.NotNull)
  def gameId = column[Int]("GAME_ID")

  def * = (id, username, gameId) <> (Player.tupled, Player.unapply)

  def game: ForeignKeyQuery[Games, Game] =
    foreignKey("PLA_GAM_FK", gameId, TableQuery[Games])(_.id)
}


class Rounds(tag: Tag) extends Table[Round](tag, "ROUNDS") {
  def id = column[Int]("ID", O.PrimaryKey)
  def word = column[String]("WORD", O.NotNull)
  def playerId = column[Int]("PLAYER_ID")

  def * = (id, word, playerId) <> (Round.tupled, Round.unapply)

  def player: ForeignKeyQuery[Players, Player] =
    foreignKey("ROU_PLA_FK", playerId, TableQuery[Players])(_.id)
}
