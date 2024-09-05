package maps

import cwinter.codecraft.core.api.DroneControllerBase
import cwinter.codecraft.core.replay.DummyDroneController
import cwinter.codecraft.core.game.{WinCondition, DroneWorldSimulator, WorldMap}

import scala.scalajs.js

case class Tutorial(
  map: WorldMap,
  enemyControllers: () => Seq[DroneControllerBase],
  missions: IndexedSeq[Subgoal],
  performMothershipSetup: Boolean,
  winConditions: Seq[WinCondition]
) extends GameConfig {
  def init(): DroneWorldSimulator = {
    val mothership = if (performMothershipSetup) {
      val m = createController(provider)
      tryInitialiseMothership(m, provider)
      m
    } else new DummyDroneController
    js.Dynamic.global.Game.mothership = mothership.asInstanceOf[js.Any]

    new DroneWorldSimulator(map.createGameConfig(mothership +: enemyControllers(), winConditions))
  }
}
