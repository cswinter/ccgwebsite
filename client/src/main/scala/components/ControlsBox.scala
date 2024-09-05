package components

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{CallbackTo, ReactComponentB, ReactEventI}

object ControlsBox {
  case class Props(
    onRestartClick: CallbackTo[Unit]
  )

  val C = ReactComponentB[Props]("TutorialText")
    .render(
      scope =>
        div(
          id := "controls-box",
          button(
            id := "button-restart-game",
            `type` := "button",
            `class` := "btn btn-default",
            onClick --> scope.props.onRestartClick,
            "Restart"
          )
      ))
    .build

  def apply(onRestartClick: CallbackTo[Unit]) = C(Props(onRestartClick))
}
