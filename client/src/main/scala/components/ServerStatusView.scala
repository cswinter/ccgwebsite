package components

import cwinter.codecraft.core.multiplayer.DetailedStatus
import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB, ReactElement}
import org.scalajs.dom.ext.Ajax
import upickle.default._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.Date
import scala.scalajs.js.timers.setTimeout
import scala.util.{Failure, Success}

object ServerStatusView {
  case class State(lastStatus: Option[DetailedStatus], networkError: Option[Throwable]) {
    def connectionStatus: Symbol =
      if (networkError.nonEmpty) 'Error
      else if (lastStatus.nonEmpty) 'Success
      else 'Connect
  }


  val C = ReactComponentB[Unit]("ServerStatusView")
    .initialState(State(None, None))
    .renderBackend[Backend]
    .componentDidMount(_.backend.start)
    .buildU

  class Backend(val self: BackendScope[Unit, State]) {
    def render(state: State) =
      div(
        serverStatsTable(state),
        h2("Game Details"),
        state.lastStatus.fold[ReactElement](
          ifEmpty = p(em("Loading games..."))
        )(status => GameStatusList(status.games))
      )

    private def serverStatsTable(state: State) = div(
      margin := "0 auto 0 auto",
      width := "400px",
      h1("Multiplayer Server Info"),
      table(
        `class` := "table table-condensed",
        width := "350px",
        tbody(
          tr(
            td("Server Status"),
            td(
              `class` := (state.connectionStatus match {
                case 'Error => "danger"
                case 'Connect => "info"
                case 'Success => "success"
              }),
              state.connectionStatus match {
                case 'Error => "Down"
                case 'Connect => "Connecting..."
                case 'Success =>
                  s"Up since ${timestampToTime(state.lastStatus.get.startTimestamp)} " +
                    timestampToDate(state.lastStatus.get.startTimestamp)
              }
            )
          ),
          tr(td("Last Response"), td(state.lastStatus.map(s => timestampToTime(s.timestamp)))),
          tr(td("Running Games"), td(state.lastStatus.map(_.games.count(_.closeReason.isEmpty)))),
          tr(td("Connections"), td(state.lastStatus.map(_.connections))),
          tr(td("Client Waiting"), td(state.lastStatus.map(_.clientWaiting.toString)))
        )
      )
    )

    def start = Callback { requestServerStatus(1000) }

    private def requestServerStatus(msNextTry: Int): Unit = try {
      Ajax.get("/ajax/multiplayerServerStatus").onComplete {
        case Success(r) =>
          val status = read[DetailedStatus](r.responseText)
          self.setState(State(Some(status), None)).runNow()
          setTimeout(1000)(requestServerStatus(1000))
        case Failure(x) =>
          println(x)
          self.modState(s => s.copy(networkError = Some(x))).runNow()
          setTimeout(msNextTry)(requestServerStatus(scala.math.min(60 * 1000, 2 * msNextTry)))
      }
    } catch {
      case x: Throwable => self.modState(s => s.copy(networkError = Some(x))).runNow()
    }

    def timestampToTime(timestamp: Long): String = {
      val date = new Date(timestamp)
      f"${date.getHours}:${date.getMinutes}%02d:${date.getSeconds}%02d"
    }

    def timestampToDate(timestamp: Long): String = {
      val date = new Date(timestamp)
      f"${date.getDate}.${date.getMonth}.${date.getFullYear}%02d"
    }
  }

  def apply() = C()
}
