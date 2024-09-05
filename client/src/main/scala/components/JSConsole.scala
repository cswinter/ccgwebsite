package components

import cwinter.codecraft.core.api.JSDroneController
import japgolly.scalajs.react.vdom.all.{s => _, _}
import japgolly.scalajs.react.{Callback, BackendScope, ReactComponentB, ReactEventI}
import org.scalajs.dom.document
import org.scalajs.dom.raw.{HTMLInputElement, KeyboardEvent}

import scala.collection.immutable.Queue
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.util.{Failure, Success, Try}


@JSExport
object JSConsole {
  case class Props(
  )

  val C = ReactComponentB[Props]("JSConsole")
    .initialState(Queue.empty[ConsoleEntry])
    .renderBackend[Backend]
    .componentDidMount(_.backend.start)
    .componentDidUpdate(_.component.backend.componentDidUpdate)
    .build

  class Backend(val self: BackendScope[Props, Queue[ConsoleEntry]]) {
    private[this] var history = IndexedSeq.empty[String]
    private[this] var historyIndex = -1

    def inputElem = document.getElementById("js-console-input").asInstanceOf[HTMLInputElement]

    def render(state: Queue[ConsoleEntry]) =
      div(`class`:="full-height",
        div(id := "js-console-output",
          for (entry <- state) yield entry match {
            case CommandResultPair(cmd, Success(r)) => div("> " + cmd, pre(`class` := "js-result", r))
            case CommandResultPair(cmd, Failure(r)) => div("> " + cmd, pre(`class` := "js-error", r.toString))
            case ExceptionLog(message) => div(pre(`class` := "js-error", message))
          }
        ),
        form(
          id := "js-console",
          onSubmit ==> handleSubmit,
          "> ", input(id := "js-console-input", `type` := "text"),
          "data-toogle".reactAttr := "tooltip",
          "data-placement".reactAttr := "right",
          "data-html".reactAttr := true
        )
      )

    def start = Callback {
      inputElem.onkeydown = handleKeydown _
      JSConsole.logError = Some(this.logError)
    }

    def componentDidUpdate = Callback {
      resetScroll()
    }

    def resetScroll(): Unit = {
      val output = document.getElementById("js-console-output")
      output.scrollTop = output.scrollHeight
    }

    def handleSubmit(e: ReactEventI) = Callback {
      e.preventDefault()
      executeCommand()
    }

    def executeCommand(): Unit = {
      val command = inputElem.value
      history :+= command
      historyIndex = history.length

      val result = Try(js.eval(command).toString)
      enqueue(CommandResultPair(command, result))
      inputElem.value = ""
    }

    def enqueue(entry: ConsoleEntry) = self.modState {
      state =>
        if (state.length < 500) state :+ entry
        else state.dropRight(1) :+ entry
    }.runNow()

    def logError(error: String): Unit = {
      enqueue(ExceptionLog(error))
    }

    def handleKeydown(e: KeyboardEvent): Unit = {
      if (e.keyCode == 38 || e.keyCode == 40) {
        e.preventDefault()
        if (e.keyCode == 38) { // arrow up
          if (historyIndex > 0) {
            historyIndex -= 1
            inputElem.value = history(historyIndex)
          }
        } else if (e.keyCode == 40) { // arrow down
          if (historyIndex < history.size - 1) {
            historyIndex += 1
            inputElem.value = history(historyIndex)
          }
        }
      }
    }
  }

  @JSExport
  var mothership: JSDroneController = null
  var logError: Option[String => Unit] = None

  def apply() = C(Props())
}


sealed trait ConsoleEntry

case class CommandResultPair(command: String, result: Try[String]) extends ConsoleEntry
case class ExceptionLog(message: String) extends ConsoleEntry

