package maps

import cwinter.codecraft.core.api._
import cwinter.codecraft.core.game.{WinCondition, Spawn, WorldMap}
import cwinter.codecraft.util.maths.{Rectangle, Vector2}
import japgolly.scalajs.react.vdom.all._


object FinalTutorial
extends Tutorial(
  map =  WorldMap(
    size = new Rectangle(-1000, 1000, -1000, 1000),
    initialDrones = Seq(
      Spawn(
        DroneSpec(constructors = 3, storageModules = 3, missileBatteries = 3, shieldGenerators = 1),
        Vector2(500, 500),
        BluePlayer,
        name = Some("mothership"),
        resources = 21
      ),
      Spawn(
        DroneSpec(constructors = 3, storageModules = 3, missileBatteries = 3, shieldGenerators = 1),
        Vector2(-500, -500),
        OrangePlayer,
        resources = 14
      )
    ),
    resourceClusters = Seq.fill(5)((10, 10))
  ),

  enemyControllers = () => Seq(TheGameMaster.level1AI()),

  missions = IndexedSeq[Subgoal](
    Subgoal(
      Seq[TagMod](
        "Welcome to the final CodeCraft tutorial! In this mission you will put together everything you ",
        "learned in the previous tutorials to build a complete AI and win your first game!"
      ),
      restartButton = true
    ),
    Subgoal(
      Seq[TagMod](
        p(
          "This tutorial will be a bit different from the others. ",
          "You are put up against an actual opponent and your ultimate goal is to destroy the enemy mothership. ",
          "Rather than giving you step by step instructions, I will just give you a couple of suggestions and leave the rest up to you. ",
          "You will probably find the 'Cheat Sheet' useful, which contains an overview all the available methods. ",
          "And of course you can still go back the previous tutorial missions. "
        )
      ),
      restartButton = true
    ),
    Subgoal(
      Seq[TagMod](
        "Here's a roadmap for a possible solution: ",
        ul(
          li("Create new files called 'Mothership' and 'Harvester'"),
          li("Modify the 'Mothership' to keep building 'Harvester' drones"),
          li("Implement scouting for the 'Harvester'"),
          li("Implement resource gathering for the 'Harvester' (as in ", a(href:="/tutorial/3", "previous tutorial"), ")"),
          li(
            "Modify the 'Mothership' controller to attack enemy drones in sight ",
            "(Hint: ", code("this.drone.dronesInSight"), " gives you an array of nearby drones. ",
            "You can tell whether a drone is an enemy by its ", code("isEnemy"), " property)"
          ),
          li("Create a new 'Warrior' controller which explores the map and attack enemies (as in ", a(href:="/tutorial/2", "second tutorial", ")")),
          li("Add a variable to the 'Mothership' controller object that allows you to keep a count of 'Harvester's. ",
            "Start building 'Warrior's after you have a sufficient number of 'Harvester's.")
        ),
        "Good luck and have fun!"
      ),
      restartButton = true
    ),
    Subgoal(
      Seq[TagMod](
        "Well done! You are now ready for the ", a(href:="/singleplayer", "singleplayer missions"),
        " which will put you up against increasingly difficult opponents. "
      ),
      Some("Follow me on Twitter <a href=\"https://twitter.com/codecraftgame\">@CodeCraftGame</a> to keep up to date with the latest changes.", HintButton),
      () => false
    )
  ),
  performMothershipSetup = true,
  WinCondition.default
)

