package maps

import cwinter.codecraft.core.api.DroneControllerBase
import cwinter.codecraft.core.game.DroneWorldSimulator


case class MultiplayerGameConfig(connection: DroneControllerBase => DroneWorldSimulator) extends GameConfig {
  def init(): DroneWorldSimulator = {
    val localPlayerController = createController(provider)
    tryInitialiseMothership(localPlayerController, provider)
    connection(localPlayerController)
  }
}

