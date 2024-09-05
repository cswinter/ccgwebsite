package controllers

import javax.inject.Inject

import betterviews._
import org.joda.time.format.DateTimeFormat
import play.api.mvc._
import play.api.{Environment, Mode}

import scala.language.postfixOps

class Application @Inject()(
  val env: Environment
) extends Controller {

  def index = Action { Ok(Index()).as("text/html") }

  def tutorialSelection = Action { Ok(TutorialSelectionView()).as("text/html") }

  def tutorial(index: Int) = Action { Ok(TutorialView(index, env.mode == Mode.Dev)).as("text/html") }

  def singleplayerSelection = Action { Ok(SingleplayerSelectionView()).as("text/html") }

  def singleplayer(index: Int) = Action { Ok(SingleplayerView(index, env.mode == Mode.Dev)).as("text/html") }

  def demo = Action { Ok(DemoView(env.mode == Mode.Dev)).as("text/html") }

  def jvm = Action { Redirect("/docs/api/index.html") }

  def documentation = Action { Ok(DocumentationView()).as("text/html") }

  val HHmmMMddyyz = DateTimeFormat.forPattern("HH:mm MM/dd/yy z")
}
