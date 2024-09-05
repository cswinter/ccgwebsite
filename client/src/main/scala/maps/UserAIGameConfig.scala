package maps

import components.JSControllerProvider
import cwinter.codecraft.core.game.DroneWorldSimulator
import cwinter.codecraft.core.api.TheGameMaster


class UserAIGameConfig(
  sources: Map[String, String]
) extends GameConfig {
  val provider2 = JSControllerProvider(sources)

  def map = TheGameMaster.defaultMap

  def init(): DroneWorldSimulator = {
    val player1Controller = createIsolatedController(provider)
    tryInitialiseMothership(player1Controller, provider)

    val player2Controller = createIsolatedController(provider2)
    tryInitialiseMothership(player2Controller, provider2)

    val controllers = Seq(player1Controller, player2Controller)

    new DroneWorldSimulator(map.createGameConfig(controllers))
  }
}
