package components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.all._
import maps.HintPosition
import org.scalajs.dom.document
import org.scalajs.dom.raw.HTMLDivElement

import scala.scalajs.js

object TutorialBox {
  case class Props(
    missionText: TagMod,
    hint: Option[(String, HintPosition)],
    previousUnlocked: Boolean,
    nextUnlocked: Boolean,
    previousOnClick: Callback,
    nextOnClick: Callback,
    onRestartClick: Callback
  )

  val C = ReactComponentB[Props]("TutorialText")
    .initialState(Unit)
    .renderBackend[Backend]
    .componentWillUpdate(_.component.backend.willUpdate)
    .componentDidUpdate(_.component.backend.didUpdate)
    .build

  class Backend(val self: BackendScope[Props, Unit.type]) {

    def render(props: Props, state: Unit) = {
      div(id := "tutorial-box",
        div(id := "tutorial-text",
          props.missionText
        ),
        div(id := "tutorial-buttons",
          button(
            id := "tutorial-button-previous",
            `type` := "button",
            `class` := "btn btn-primary",
            onClick --> props.previousOnClick,
            if (props.previousUnlocked) Seq[ReactElement]() else Seq(disabled := true),
            "Previous"
          ),
          props.hint.map(hint =>
            button(
              id := "tutorial-button-hint",
              `type` := "button",
              `class` := "btn btn-secondary",
              "data-toogle".reactAttr := "tooltip",
              "data-placement".reactAttr := "bottom",
              "data-html".reactAttr := true,
              onClick --> CallbackTo[Unit] { js.eval(s"$$('${hint._2.selector}').tooltip('toggle')") },
              "Show Hint"
            )
          ).toSeq,
          button(
            id := "tutorial-button-next",
            `type` := "button",
            `class` := "btn btn-primary",
            onClick --> props.nextOnClick,
            if (props.nextUnlocked) Seq[ReactElement]() else Seq(disabled := true),
            "Next",

            "data-toogle".reactAttr := "tooltip",
            "data-placement".reactAttr := "right",
            "data-html".reactAttr := true
          )
        )
      )
    }


    def willUpdate = Callback {
      // the tooltips plugin might have injected a node,
      // we need to remove this before update otherwise strange things happen
      js.eval(s"$$('#tutorial-buttons .tooltip').remove()")
    }

    def didUpdate = Callback {
      val div = document.getElementById("tutorial-text").asInstanceOf[HTMLDivElement]
      div.scrollTop = 0
    }
  }

  def apply(
    missionText: TagMod,
    hint: Option[(String, HintPosition)],
    previousUnlocked: Boolean,
    nextUnlocked: Boolean,
    previousOnClick: Callback,
    nextOnClick: Callback,
    onRestartClick: Callback
  ) = C(Props(missionText, hint, previousUnlocked, nextUnlocked, previousOnClick, nextOnClick, onRestartClick))
}

