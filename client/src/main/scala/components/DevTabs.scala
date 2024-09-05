package components

import cwinter.codecraft.core.api.JSDroneController
import japgolly.scalajs.react.vdom.all.{s => _, _}
import japgolly.scalajs.react.{BackendScope, ReactComponentB}

import scala.scalajs.js.annotation.JSExport
import scala.util.Try


@JSExport
object DevTabs {
  case class Props(reducedHeight: Boolean)
  type State = Seq[(String, Try[String])]

  val C = ReactComponentB[Props]("DevTabs")
    .initialState(Seq.empty[(String, Try[String])])
    .renderBackend[Backend]
    .build


  class Backend(val self: BackendScope[Props, State]) {
    def render(props: Props, state: State) = {
      div(`class`:=(if (props.reducedHeight) "editor-tabs-col-small" else "editor-tabs-col"),
        ul(`class`:="nav nav-tabs navbar-inverse", role:="tablist",
          tab("console", "Console", active=true),
          tab("editor-pane", "Editor", active=false),
          tab("cheat-sheet", "Cheat Sheet", active=false)
        ),
        div(`class`:="tab-content editor-tab-pane",
          div(role:="tabpanel", `class`:="tab-pane active full-height", id:="console",
            JSConsole()
          ),
          div(role:="tabpanel", `class`:="tab-pane full-height", id:="editor-pane",
            JSEditor()
          ),
          div(role:="tabpanel", `class`:="tab-pane full-height bold", id:="cheat-sheet",
            CheatSheet.C()
          )
        )
      )
    }

    def tab(name: String, text: String, active: Boolean) = {
      li(
        role := "presentation",
        `class` := "nav-tab-dark",
        if (active) `class` := "active" else Seq.empty[TagMod],
        a(
          href := s"#$name",
          "aria-controls".reactAttr := name,
          role := "tab",
          "data-toggle".reactAttr := "tab",
          text
        )
      )
    }
  }

  @JSExport
  var mothership: JSDroneController = null

  def apply(reducedHeight: Boolean) = C(Props(reducedHeight))
}

