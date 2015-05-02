package com.buzzfactory.lw

import java.sql.Date


/**
 * Created by tetio on 02/05/15.
 */
case class Game (id: Int, doc: Date, state: String)

case class UsedWord (word: String, gameId: Int)

case class Player (id: Int, username: String, gameId: Int)

case class Round (id: Int, word: String, playerId: Int)
