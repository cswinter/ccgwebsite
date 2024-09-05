package components

import upickle.default._

case class SingleplayerGame(level: Int) extends ReactComponent {
  require(level <= 7)
}
case class TutorialGame(level: Int) extends ReactComponent {
  require(level <= 4)
}
case object GameplayDemo extends ReactComponent
case object MultiplayerServerStatus extends ReactComponent

sealed trait ReactComponent {
  def serialize: String = write(this)
}
