package maps

import cwinter.codecraft.core.game.{DroneWorldSimulator, WorldMap}
import cwinter.codecraft.core.api.{DroneControllerBase, TheGameMaster}


case class SingleplayerMission(
  opponent: () => DroneControllerBase,
  map: WorldMap= TheGameMaster.defaultMap
) extends GameConfig {
  def init(): DroneWorldSimulator = {
    val mothership = createController(provider)
    initialiseMothershipOrEmitWarning(mothership, provider)

    new DroneWorldSimulator(map.createGameConfig(Seq(mothership, opponent())))
  }
}

object SingleplayerLevel1 extends SingleplayerMission(TheGameMaster.level1AI, TheGameMaster.level1Map)
object SingleplayerLevel2 extends SingleplayerMission(TheGameMaster.level2AI)
object SingleplayerLevel3 extends SingleplayerMission(TheGameMaster.bonusLevelAI)
object SingleplayerLevel4 extends SingleplayerMission(TheGameMaster.level4AI)
object SingleplayerLevel5 extends SingleplayerMission(TheGameMaster.level5AI)
object SingleplayerLevel6 extends SingleplayerMission(TheGameMaster.level6AI)
object SingleplayerLevel7 extends SingleplayerMission(TheGameMaster.level7AI)

