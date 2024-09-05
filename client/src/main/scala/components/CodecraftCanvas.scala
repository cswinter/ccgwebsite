package components

import cwinter.codecraft.core.api.TheGameMaster
import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB}
import org.scalajs.dom.{document, html}

import scala.scalajs.js.annotation.JSExport


@JSExport
object CodecraftCanvas {
  val C = ReactComponentB[Unit]("CodecraftCanvas")
    .initialState(Unit)
    .renderBackend[Backend]
    .componentDidMount(_.backend.start)
    .build

  class Backend(val self: BackendScope[Unit, Unit.type]) {
    def render() = {
      div(`class`:="full-height-rel",
        canvas(id:="webgl-canvas", tabIndex:=1),
        div(id:="text-container", tabIndex:= -1),
        div(id:="text-test-container", tabIndex:= -1)
      )
    }

    def start = Callback {
      val canvas = document.getElementById("webgl-canvas").asInstanceOf[html.Canvas]
      TheGameMaster.canvas = canvas
      canvas.focus()
    }
  }

  def apply() = C(Unit)
}

