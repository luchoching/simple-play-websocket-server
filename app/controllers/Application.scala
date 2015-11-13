package controllers

import akka.actor.{Actor, Props, ActorRef}
import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.mvc._
import play.api.Play.current

class Application extends Controller {

  def index = Action {
    Ok("Hi!")
  }

  def wsHello = WebSocket.using[String] { request =>
    val in = Iteratee.ignore[String]
    val out = Enumerator("Hello!!").andThen(Enumerator.eof)
    (in, out)
  }

  object EchoWebScoketActor {
    def props(out: ActorRef) = Props(new EchoWebScoketActor(out))
  }

  class EchoWebScoketActor(out: ActorRef) extends Actor {
    def receive = {
      case msg: String => out ! msg
    }
  }

  def wsActorEcho = WebSocket.acceptWithActor[String, String]{
    request => out => EchoWebScoketActor.props(out)
  }

}
