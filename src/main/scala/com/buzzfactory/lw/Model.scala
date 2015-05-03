package com.buzzfactory.lw

import java.sql.Timestamp


/**
 * Created by tetio on 02/05/15.
 */
case class Game (id: Int, doc: Timestamp, state: String)
//  usedWords: Seq[String]

case class UsedWord (word: String, gameId: Int)

case class Player (id: Int, username: String, gameId: Int)

case class Round (id: Int, word: String, playerId: Int)
