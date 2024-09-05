package components

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{Callback, ReactElement, BackendScope, ReactComponentB}
import upickle.default._
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global


object SourceCodeView {

  val C = ReactComponentB[String]("SourceCodeView")
    .initialState(None: Option[Map[String, String]])
    .renderBackend[Backend]
    .componentDidMount(_.backend.start)
    .componentDidUpdate(_.component.backend.update)
    .build


  class Backend(
    val self: BackendScope[String, Option[Map[String, String]]]
  ) {
    def render(state: Option[Map[String, String]]): ReactElement = {
      state.map[ReactElement](sources =>
        div(
          for ((filename, source) <- sources)
            yield {
              div(
                h4(filename),
                pre(code(
                  `class`:="javascript",
                  source
                ))
              )
            }
        )
      ).getOrElse(div("Downloading sources..."))
    }

    def start = self.props.map(url =>
        Ajax.get(url).onSuccess {
          case response =>
            val text = response.responseText
            val sources = read[Map[String, String]](text)
            self.setState(Some(sources)).runNow()
        }
      )

    def update = Callback {
      import scalajs.js.Dynamic.{global => g}
      g.hljs.initHighlighting()
    }
  }

  def apply(url: String) = C(url)
}
