package maps

import cwinter.codecraft.core.game.DroneWorldSimulator
import cwinter.codecraft.core.api.TheGameMaster


case class ReplayConfig(replayText: String) extends GameConfig {
  override def init(): DroneWorldSimulator =
    TheGameMaster.createReplaySimulator(replayText)
}

