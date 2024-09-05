package components

import japgolly.scalajs.react.vdom.all._
import scala.scalajs.js.Dynamic.{global => g}


class Alert(level: String, id: String, content: TagMod*) {
  def vdom: TagMod =
    div(
      japgolly.scalajs.react.vdom.all.id := id,
      `class` := s"alert $level alert-dismissible collapse",
      role := "alert",
      button(
        `type` := "button",
        `class` := "close",
        "data-dismiss".reactAttr := "alert",
        aria.label := "Close",
        span(
          aria.hidden := "true",
          "\u00D7" // Unicode Character 'Multiplication Sign'
        )
      ),
      content
    )

  def show(duration: Int): Unit = {
    val elem = findElem
    elem.alert()
    elem.fadeTo(duration, 500).slideUp(500, () => elem.hide())
  }

  def showPermanently(): Unit = {
    findElem.show()
  }

  def findElem = g.$(s"#$id")
}

class AlertWarning(id: String, content: TagMod*)
  extends Alert("alert-warning", id, content: _*)

class AlertSuccess(id: String, content: TagMod*)
  extends Alert("alert-success", id, content: _*)

class AlertDanger(id: String, content: TagMod*)
  extends Alert("alert-danger", id, content: _*)

