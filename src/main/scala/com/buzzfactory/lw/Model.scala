package com.buzzfactory.lw

import java.sql.Timestamp


/**
 * Created by tetio on 02/05/15.
 */
case class Game (id: Option[Int] = None, doc: Timestamp, numPlayers: Int, language: String, state: String, word: String, letters: Seq[String], players: Seq[String], usedWords: Seq[String])
//  usedWords: Seq[String]

case class UsedWord (word: String, gameId: Int)

case class Player (id: Option[Int] = None, username: String, gameId: Int)

case class Round (id: Int, word: String, playerId: Int)

case class Word(word: String)

case class Pair(id: Int, word: String)
