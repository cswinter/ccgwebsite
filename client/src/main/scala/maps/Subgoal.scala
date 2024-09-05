package maps

import japgolly.scalajs.react.vdom.TagMod

case class Subgoal(
  text: TagMod,
  hint: Option[(String, HintPosition)] = None,
  successCriterion: () => Boolean = () => true,
  restartButton: Boolean = false
)

sealed trait HintPosition {
  def selector: String
}
object HintButton extends HintPosition {
  override final def selector = "#tutorial-button-hint"
}
object NextButton extends HintPosition {
  override final def selector = "#tutorial-button-next"
}
object ConsoleField extends HintPosition {
  override final def selector = "#js-console"
}
case class CustomHintPosition(selector: String) extends HintPosition

