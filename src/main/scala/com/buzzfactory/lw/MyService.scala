package com.buzzfactory.lw

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(lwRoutes)
}


// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {

  def getJson(route: Route) = get {
    respondWithMediaType(MediaTypes.`application/json`) {
      route
    }
  }

  def postJson(route: Route) = post {
    respondWithMediaType(MediaTypes.`application/json`) {
      route
    }
  }

  def putJson(route: Route) = put {
    respondWithMediaType(MediaTypes.`application/json`) {
      route
    }
  }

  lazy val wordRoute = {
    getJson {
      path("validateWord" / RestPath) { word =>
        complete {
          WordHandler.validate(word.toString)
        }
      }
    } ~
    getJson {
      path("word" / RestPath) { word =>
        complete {
          WordHandler.findWord(word.toString)
        }
      }
    }

  }


  lazy val gameRoute = {
    getJson {
      path("game" / "all") {
        complete {
          GameHandler.findAllGames()
        }
      }
    } ~
    getJson {
      path("game" / "all2") {
        complete {
          GameHandler.findAll2()
        }
      }
    } ~
    getJson {
      path("game" / IntNumber) { index =>
        complete {
          GameHandler.findById(index)
        }
      }
    } ~
    putJson {
      path("game") {
        formFields("username", "numplayers".as[Int], "language"?) { (username, numPlayers, language) =>
          complete {
            GameHandler.newGame(username, numPlayers, language.getOrElse("CA"))
          }
        }
      }
    }
  }





  lazy val myRoute = {
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Say hello to
                  <i>spray-routing</i>
                  on
                  <i>spray-can</i>
                  !</h1>
              </body>
            </html>
          }
        }
      }
    } ~
    getJson {
      path("lw" / "init") {
        complete {
          GameHandler.findById2(0)
        }
      }
    }
  }

  val lwRoutes = wordRoute ~ gameRoute ~ myRoute

}