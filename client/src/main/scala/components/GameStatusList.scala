package components

import cwinter.codecraft.core.multiplayer.GameStatus
import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}

import scala.scalajs.js.Date

object GameStatusList {
  type State = Unit
  type Props = Seq[GameStatus]

  val C = ReactComponentB[Props]("ServerStatusView")
    .initialState(None: State)
    .renderBackend[Backend]
    .build

  class Backend(val self: BackendScope[Props, State]) {
    def render(props: Props) =
      div(
        for (GameStatus(completed, fps, averageFPS, timestep, startTimestamp, endTimestampOpt,
            msSinceLastResponse, currentPhase, rawUp, rawDown) <- props.sortBy(-_.startTimestamp))
          yield {
            val up = if (completed.nonEmpty) f"${rawUp / 1000}%.0f" else f"$rawUp%.1f"
            val down = if (completed.nonEmpty) f"${rawDown / 1000}%.0f" else f"$rawDown%.1f"
            div(
              width := "300px",
              display := "inline-block",
              marginLeft := "10px",
              marginRight := "10px",
              `class` := "panel",
              `class` := (
                if (completed.nonEmpty) "panel-default"
                else if (msSinceLastResponse > 1000) "panel-warning"
                else "panel-success"
                ),
              div(
                `class` := "panel-heading",
                h3(
                  `class` := "panel-title",
                  completed match {
                    case None =>
                      if (msSinceLastResponse < 1000) s"Running"
                      else s"Stalled ($currentPhase, ${msSinceLastResponse / 1000}s)"
                    case Some(reason) => s"$reason"
                  }
                )
              ),
              div(
                `class` := "panel-body",
                endTimestampOpt match {
                  case None => s"Game Started: ${timestampToTime(startTimestamp)}"
                  case Some(end) =>
                    s"When: ${timestampToTime(startTimestamp)} - ${timestampToTime(end)} (${timestampToDate(startTimestamp)})"
                }, br,
                s"Timestep: $timestep", br,
                s"FPS: $fps", br,
                s"Average FPS: $averageFPS", br,
                if (completed.nonEmpty) "Total Data (KB): " else "Bandwidth (Kpbs): ",
                glyphicon("arrow-up"), up, glyphicon("arrow-down"), down
              )
            )
          }
      )
  }

  def glyphicon(name: String) = span(`class`:=s"glyphicon glyphicon-$name")

  def timestampToTime(timestamp: Long): String = {
    val date = new Date(timestamp)
    f"${date.getHours}:${date.getMinutes}%02d:${date.getSeconds}%02d"
  }

  def timestampToDate(timestamp: Long): String = {
    val date = new Date(timestamp)
    f"${date.getDate}.${date.getMonth}.${date.getFullYear}%02d"
  }

  def apply(props: Props) = C(props)
}
