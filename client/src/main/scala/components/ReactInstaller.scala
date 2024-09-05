package components

import cwinter.codecraft.core.api.{TheGameMaster, JSDroneController}
import japgolly.scalajs.react.{ReactDOM, React}
import maps._
import org.scalajs.dom.raw.HTMLDivElement
import upickle.default._
import org.scalajs.dom.document

import scala.scalajs.js.annotation.JSExport

@JSExport
object ReactInstaller {
  @JSExport
  def install(): Unit = {
    val reactContainer = document.getElementById("react-container").asInstanceOf[HTMLDivElement]
    val data = read[ReactComponent](reactContainer.getAttribute("data-component-json"))
    val singleplayerMissions = Map(
      1 -> SingleplayerLevel1,
      2 -> SingleplayerLevel2,
      3 -> SingleplayerLevel3,
      4 -> SingleplayerLevel4,
      5 -> SingleplayerLevel5,
      6 -> SingleplayerLevel6,
      7 -> SingleplayerLevel7
    )

    val reactComponent = data match {
      case TutorialGame(1) => CodecraftJS(ConsoleTutorial)
      case TutorialGame(2) => CodecraftJS(EditorTutorial)
      case TutorialGame(3) => CodecraftJS(EconomyTutorial)
      case TutorialGame(4) => CodecraftJS(FinalTutorial)
      case TutorialGame(l) => throw new IllegalArgumentException(s"There is no tutorial level $l.")

      case SingleplayerGame(l) =>
        singleplayerMissions.get(l) match {
          case None => throw new IllegalArgumentException(s"There is not singleplayer level $l")
          case Some(mission) => CodecraftJS(mission)
        }

      case MultiplayerServerStatus => ServerStatusView()

      case GameplayDemo => GameplayDemoView()
    }

    ReactDOM.render(reactComponent, reactContainer)
  }
}
