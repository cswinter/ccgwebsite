package components

import cwinter.codecraft.core.game.DroneWorldSimulator
import cwinter.codecraft.core.api.{JSDroneController, DroneSpec, TheGameMaster}
import cwinter.codecraft.util.maths.Vector2
import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react._
import maps._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.JSExport

@JSExport
object CodecraftJS {
  private[this] var _runningGame: Option[DroneWorldSimulator] = None
  def runningGame = _runningGame

  case class Props(gameConfig: maps.GameConfig) {
    def isTutorial = gameConfig.isInstanceOf[Tutorial]
  }
  type MissionIndex = Int

  val MothershipInitAlert = new AlertWarning(
    "warning-mothership-init",
    strong("Warning: "),
    "Could not initialise 'Mothership' controller. (See console for details)."
  )

  val C = ReactComponentB[Props]("CodecraftJS")
    .initialState(0)
    .renderBackend[Backend]
    .componentDidMount(_.backend.start)
    .componentWillUnmount(_.backend.stop)
    .componentDidUpdate(_.component.backend.componentDidUpdate)
    .build

  class Backend(val self: BackendScope[Props, MissionIndex]) {

    def render(props: Props, state: MissionIndex): ReactElement = {
      div(
        `class` := "game-container",
        MothershipInitAlert.vdom,
        JSEditor.NoWebStorageAlert.vdom,
        JSEditor.OldCodeVersionAlert.vdom,
        div(
          `class` := "editor-col full-height",
          props.gameConfig match {
            case tutorial: Tutorial =>
              val mission = tutorial.missions(state)
              TutorialBox(
                mission.text,
                mission.hint,
                state > 0,
                mission.successCriterion(),
                previousMission,
                nextMission,
                restartLevel
              )
            case _ => EmptyTag
          },
          ControlsBox(restartLevel),
          DevTabs(props.isTutorial)
        ),
        div(`class` := "game-col", CodecraftCanvas())
      )
    }

    def start = startGame >> installTooltips

    def startGame = self.props.map { props =>
      val config = props.gameConfig
      val simulator = config.init()
      installJSAPI(simulator)
      _runningGame = Some(TheGameMaster.run(simulator))
    }

    def installJSAPI(simulator: DroneWorldSimulator): Unit = {
      if (js.typeOf(g.Game) == "undefined") g.Game = new js.Object

      g.Game.vector2 = Vector2.apply(_: Double, _: Double)

      def intOr0(value: js.Dynamic): Int = value.asInstanceOf[Any] match {
        case int: Int => int
        case _ => 0
      }

      g.Game.spec = (x: js.Dynamic) =>
        DroneSpec.apply(
          intOr0(x.storageModules) + intOr0(x.refineries),
          intOr0(x.missileBatteries),
          intOr0(x.constructors),
          intOr0(x.engines),
          intOr0(x.shieldGenerators)
      )

      val named = simulator.namedDrones.toJSDictionary
      g.Game.drones = named
    }

    def componentDidUpdate = installTooltips

    def stop = Callback { TheGameMaster.stop() }

    def nextMission = self.modState { mission =>
      mission + 1
    }

    def previousMission = self.modState { mission =>
      require(mission > 0)
      mission - 1
    }

    def restartLevel = stop >> start

    def installTooltips = {
      for {
        props <- self.props
        state <- self.state
      } yield {
        props.gameConfig match {
          case tutorial: Tutorial =>
            val hint = tutorial.missions(state).hint.map(_._1).getOrElse("")
            for (selector <- Seq("#js-console", "#tutorial-button-hint", "#tutorial-button-next")) {
              js.eval(s"$$('$selector').tooltip({trigger: 'manual'})")
              js.eval(
                s"$$('$selector').tooltip('hide').attr('data-original-title', '$hint').tooltip('fixTitle')")
            }
          case _ => // don't need to do anything
        }
      }
    }
  }

  def exceptionLogger(throwable: Throwable, function: String, drone: JSDroneController): Unit = {
    for (logError <- JSConsole.logError) {
      val message =
        s"Exception in '$function' function of '${drone.controllerName}':\n$throwable"
      logError(message)
    }
  }

  def apply(config: maps.GameConfig) = C(Props(config))
}
