package components

import cwinter.codecraft.core.game.{Settings, DroneWorldSimulator}
import cwinter.codecraft.core.api.{DroneControllerBase, TheGameMaster}
import japgolly.scalajs.react.vdom.all
import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{ReactElement, BackendScope, Callback, ReactComponentB, ReactEventI}
import org.scalajs.dom.document
import org.scalajs.dom.raw.HTMLSelectElement

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport


@JSExport
object GameplayDemoView {
  case class State(
    showSidebar: Boolean = true,
    orangeAI: String = "Destroyer",
    blueAI: String = "Replicator",
    map: String = "Default Map",
    fps: Int = 60,
    targetFPS: Int = 60,
    graphicsSetting: GraphicsSetting = FancyGraphics
  )


  val C = ReactComponentB[Unit]("GameplayDemo")
    .initialState(State())
    .renderBackend[Backend]
    .componentWillMount(_.backend.willMount)
    .componentDidMount(_.backend.start)
    .componentWillUnmount(_.backend.stop)
    .buildU

  val players = Map(
    "Replicator" -> (() => TheGameMaster.replicatorAI()),
    "Destroyer" -> TheGameMaster.destroyerAI _,
    "Greedy Replicator" -> (() => TheGameMaster.replicatorAI(greedy = true)),
    "Confident Replicator" -> (() => TheGameMaster.replicatorAI(confident = true)),
    "Aggressive Replicator" -> (() => TheGameMaster.replicatorAI(aggressive = true))
  )
  val aiKeys = players.keys.toSeq.sorted

  val maps = Map(
    "Large Map" -> (() => TheGameMaster.largeMap),
    "Default Map" -> (() => TheGameMaster.defaultMap),
    "Small Map" -> (() => TheGameMaster.smallMap)
  )
  val mapKeys = maps.keys.toSeq

  sealed trait GraphicsSetting { def apply(settings: Settings) }
  object FancyGraphics extends GraphicsSetting { def apply(settings: Settings) = settings.setBestGraphics() }
  object FastGraphics extends GraphicsSetting { def apply(settings: Settings) = settings.setFastestGraphics() }
  val graphicsSettings = Map("Fancy Graphics" -> FancyGraphics, "Fast Graphics" -> FastGraphics)
  val graphicsKeys = graphicsSettings.keys.toSeq


  class Backend(val self: BackendScope[Unit, State]) {
    private var runningGame = Option.empty[DroneWorldSimulator]

    def render(state: State) =
      div(`class` := "game-container",
        if (state.showSidebar) fullSidebar(state)
        else collapsedSidebar(state),

        div(
          float := "right",
          width := (if (state.showSidebar) "calc(100% - 350px)" else "calc(100% - 50px"),
          height := "100%",
          CodecraftCanvas()
        )
      )

    def fullSidebar(state: State) =
      div(
        float := "left",
        width := "350px",
        height := "100%",
        backgroundColor := "#222",
        color := "white",

        div(`class` := "demo-sidebar",
          margin := "10px",

          h3("Graphics"),
          div(strong("Target FPS: "), state.targetFPS, marginBottom := "7px"),
          div(strong("Actual FPS: "), state.fps, marginBottom := "7px"),
          div(
            `class` := "form-inline",
            marginBottom := "7px",
            selectGraphics(state.graphicsSetting match {
              case FancyGraphics => "Fancy Graphics"
              case FastGraphics => "Fast Graphics"
            })
          ),
          div(`class`:= "form-inline", hideSidebarButton),

          h3("Game Config"),
          div(
            `class` := "form-inline",
            marginBottom := "7px",
            selectAI("Orange", state.orangeAI),
            " vs. ",
            selectAI("Blue", state.blueAI)
          ),

          div(
            `class` := "form-inline",
            marginBottom := "7px",
            selectMap(state.map)
          ),

          restartButton,

          h3("Controls"),
          hotkeys
        )
      )

    def collapsedSidebar(state: State) =
      div(
        float := "left",
        width := "50px",
        height := "100%",
        backgroundColor := "#222",
        color := "white",

        div(
          `class` := "collapsed-demo-sidebar",
          margin := "5px",
          div(state.fps, "/", state.targetFPS, textAlign := "center"),
          restoreSidebarButton,
          collapsedRestartButton
        )
      )

    def hideSidebarButton =
      button(
        id := "button-hide-sidebar",
        `type` := "button",
        `class` := "btn btn-default",
        onClick --> self.modState(_.copy(showSidebar = false)),
        "Collapse Sidebar"
      )

    def selectAI(identifier: String, selection: String) =
      select(
        borderColor := (if (identifier == "Blue") "blue" else "orange"),
        width := "150px",
        `class` := "form-control",
        id := identifier,
        value := selection,
        onChange ==> aiSelectionChanged(identifier),
        for ((ai, i) <- aiKeys.zipWithIndex)
          yield option(value := ai, ai, key := i)
      )

    def selectMap(selection: String) =
      select(
        width := "150px",
        `class` := "form-control",
        id := "select-map",
        value := selection,
        onChange ==> mapSelectionChanged,
        for ((map, i) <- mapKeys.zipWithIndex)
          yield option(value := map, map, key := i)
      )

    def selectGraphics(selection: String) =
      select(
        width := "150px",
        `class` := "form-control",
        id := "select-map",
        value := selection,
        onChange ==> graphicsChanged,
        for ((s, i) <- graphicsKeys.zipWithIndex)
          yield option(value := s, s, key := i)
      )

    def aiSelectionChanged(id: String)(e: ReactEventI) = self.modState {
      state =>
        if (id == "Blue") state.copy(blueAI = e.target.value)
        else state.copy(orangeAI = e.target.value)
    }

    def mapSelectionChanged(e: ReactEventI) = self.modState(_.copy(map = e.target.value))

    def graphicsChanged(e: ReactEventI) = {
      val settings = graphicsSettings(e.target.value)
      self.modState(_.copy(graphicsSetting = settings)) >> Callback {
        for (s <- runningGame) settings.apply(s.settings)
      }
    }

    def restartButton =
      button(
        id := "button-restart-game",
        `type` := "button",
        `class` := "btn btn-default",
        onClick --> restart,
        "Restart Game"
      )

    def hotkeys = p(
      `class` := "hotkeys-dark",
      kbd("W"), ",", kbd("A"), ",", kbd("S"), ",", kbd("D"), " or click and drag to move camera", br,
      kbd("Q"), "/", kbd("E"), " or mouse wheel to zoom in/out", br,
      kbd("<SPACEBAR>"), " to pause/resume", br,
      kbd("1"), "/", kbd("2"), " to toggle sight radius/weapons range", br,
      kbd("F"), "/", kbd("R"), " to increase/decrease game speed"
    )

    def restoreSidebarButton =
      Button(
        id = "button-restore-sidebar",
        onClick = self.modState(_.copy(showSidebar = true)),
        content = span(`class` := "glyphicon glyphicon-menu-right")
      )

    def collapsedRestartButton =
      Button(
        id = "button-restart-game",
        onClick = restart,
        content = span(`class` := "glyphicon glyphicon-repeat")
      )

    def Button(id: String, onClick: Callback, content: ReactElement*) =
      button(
        marginBottom := "5px",
        all.id := id,
        `type` := "button",
        `class` := "btn btn-default",
        all.onClick --> onClick,
        content
      )

    def start = self.state.map {
      state =>
        val controller1 = players(state.blueAI)()
        val controller2 = players(state.orangeAI)()
        val map = maps(state.map)()
        val simulator = TheGameMaster.createSimulator(controller1, controller2, map)
        simulator.settings.allowMessages = false
        state.graphicsSetting.apply(simulator.settings)
        runningGame = Some(TheGameMaster.run(simulator))
    }

    def selectedAI(player: String): DroneControllerBase = {
      val select = document.getElementById(player).asInstanceOf[HTMLSelectElement]
      val aiName = select.value
      players(aiName)()
    }

    def stop = Callback {
      TheGameMaster.stop()
    }

    def restart = stop >> start

    def willMount = Callback {
      js.timers.setInterval(1000) {
        for (fps <- TheGameMaster.currentFPS)
          self.modState(_.copy(fps = fps)).runNow()
      }
      js.timers.setInterval(50) {
        for (simulator <- runningGame)
          if (self.state.runNow().targetFPS != simulator.framerateTarget)
            self.modState(_.copy(targetFPS = simulator.framerateTarget)).runNow()
      }
      new Settings(recordReplays = false).setAsDefault()
    }
  }

  def apply() = C()
}

