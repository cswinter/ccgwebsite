package maps

import cwinter.codecraft.core.api._
import cwinter.codecraft.core.replay.DummyDroneController
import cwinter.codecraft.core.game.{DestroyAllEnemies, Spawn, WorldMap}
import cwinter.codecraft.util.maths.{Rectangle, Vector2}
import japgolly.scalajs.react.vdom.all._

object ConsoleTutorial
extends Tutorial(
  map = WorldMap(
    size = new Rectangle(-1000, 1000, -1000, 1000),
    initialDrones = Seq(
      Spawn(
        DroneSpec(missileBatteries = 2),
        Vector2(0, 0),
        BluePlayer, name = Some("scout")
      ),
      Spawn(
        DroneSpec(shieldGenerators = 1),
        Vector2(500, -500),
        OrangePlayer,
        name = Some("enemy")
      )
    ),
    resourceCount = 50
  ),

  enemyControllers = () => Seq(new DummyDroneController),

  missions = IndexedSeq[Subgoal](
    Subgoal(
      "Welcome to the CodeCraft tutorial! " +
        "In this mission you will learn the basics of the interface and how to give simple commands to your drones using the console.",
      Some("Just click the next button.", NextButton)
    ),
    Subgoal(
      Seq[TagMod](
        "On the right of the window you can see the game world. ",
        "You can move around in it using the keys ", kbd("WASD"), " or by clicking and dragging. ",
        "You can zoom in and out using ", kbd("QE"), " or the mouse wheel."
      )
    ),
    Subgoal(
      Seq[TagMod](
        "In the bottom left corner is the console. ",
        "You can write any JavaScript into it and run it by pressing enter. ",
        "Go ahead and try it out!",
        br,
        "(If you prefer, you can also use your browser's inbuilt Javascript console.)"
      ),
      Some("Write something like <code>23 + 19</code> or <code>alert(\"hello, world\")</code>", ConsoleField)
    ),
    Subgoal(
      Seq[TagMod](
        "At the center of the game world you should a square shaped thingy. ",
        "This is your drone. ",
        "This drone is available in JavaScript as ", code("Game.mothership"), ". ",
        "Try running ", code("Game.mothership.displayString"),
        " to print some information about this drone to the console. "
      )
    ),
    Subgoal(
      Seq[TagMod](
        "Now let's move the drone to a different position! ",
        "To do so, you can call the ", code("moveTo(x, y)"),
        " method on ", code("Game.mothership"), " to instruct it to the position (x, y). "
      ),
      Option("Just write <code>Game.mothership.moveTo(500, 500)</code> and press enter.", ConsoleField)
    ),
    Subgoal(
      Seq[TagMod](
        "Fantastic! ",
        "As you might have already noticed, there is an enemy drone on this map, at coordinate (500, -500). ",
        "Your final task will be to destroy this drone. ",
        "But first you will have to get within weapon range, so move your drone to (500, -500) with the ",
        code("moveTo"), " command."
      )
    ),
    Subgoal(
      Seq[TagMod](
        "Alright! Now that you are positioned close to the enemy drone, it's time to destroy it. ",
        "For this, use your drone's ", code("fireMissilesAt(target)"), " method. ",
        "You have to supply this method with a reference to the drone that you want to shoot at, which is ",
        code("Game.drones.enemy"), ".",
        br,
        "The enemy drone is protected by powerful shields, so you will need to fire missiles several times to destroy it. ",
        "Pro tip: you can use the up arrow key to browse the commands you previously entered in the console."
      ),
      Option("The code you are looking for is <code>Game.mothership.fireMissilesAt(Game.drones.enemy)</code>", HintButton)
    ),
    Subgoal(
      Seq[TagMod](
        "Well done, you have completed the first tutorial! In the next tutorial, you will learn how to automate your drones. ",
        a(href:="/tutorial/2", "Click here to continue.")
      ),
      successCriterion = () => false
    )
  ),
  performMothershipSetup = false,
  Seq(DestroyAllEnemies)
)

