package maps

import components.{CodecraftJS, CodecraftCanvas}
import cwinter.codecraft.core.api._
import cwinter.codecraft.core.game.{DestroyAllEnemies, DroneWorldSimulator, Spawn, WorldMap}
import cwinter.codecraft.graphics.engine.Debug
import cwinter.codecraft.util.maths.{Vector2, Rectangle}
import japgolly.scalajs.react.vdom.all._


object EditorTutorial extends {
  val enemySpawnLocations =
    Seq(
      (-500, 500), (240, -750), (-800, 300), (100, 200), (-100, -160),
      (105, 454), (500, 500), (800, 0), (-800, 0), (0, 800), (0, -800)
    )
} with Tutorial(
  map = WorldMap(
    size = new Rectangle(-1000, 1000, -1000, 1000),
    initialDrones = Seq(
      Spawn(DroneSpec(missileBatteries = 2, engines = 2), Vector2(0, 0), BluePlayer, name = Some("scout"))
    ) ++ {
      for ((x, y) <- enemySpawnLocations)
        yield Spawn(DroneSpec(storageModules = 1), Vector2(x, y), OrangePlayer)
    },
    resourceCount = 50
  ),

  enemyControllers = () => enemySpawnLocations.map(_ => new TimidDrone),

  missions = IndexedSeq[Subgoal](
    Subgoal(
      "Welcome to the second CodeCraft tutorial! In this mission you will learn the basics of how to automate your drones.",
      Some("Just click the \"Next\" button.", NextButton)
    ),
    Subgoal(
      "The first thing you need to do is to click on the \"Editor\" tab to open the editor."
    ),
    Subgoal(
      Seq[TagMod](
        "You should now have a file 'Mothership.js' open, with code that looks something like this: ",
        br,
        pre("var _droneController = {\n  onTick: function() {\n  }\n};"),
        "If there is no such file, create one."
      )
    ),
    Subgoal(
      Seq[TagMod](
        "When the game first starts, it will look whether there is a file named 'Mothership'. ",
        "If that is the case, it will automatically associate the code in that file to your first drone. ",
        "After you make changes to the file, you can click the 'Restart' button to restart the game and update the code ."
      )
    ),
    Subgoal(
      Seq[TagMod](
        "So lets take a closer look at the code inside 'Mothership.js'. ",
        "Note that the code creates an object that defines an ", code("onTick"), " method. ",
        "This ", code("onTick"), " function will be called automatically by the game on every timestep. ",
        "Inside the function, you can access the associated drone as ", code("this.drone"), "."
      )
    ),
    Subgoal(
      Seq[TagMod](
        "So let's try it out! ",
        "Copy the following code into the ", code("onTick"), " function: ",
        pre("var x = Math.random() * 1800 - 900;\nvar y = Math.random() * 1800 - 900;\nthis.drone.moveTo(x, y);"),
        "Once you're done, click the 'Restart' button to activate your code."
      )
    ),
    Subgoal(
      Seq[TagMod](
        "Your drone should now be moving frantically; on every timestep it is moving towards a new random position! ",
        "Let's help it calm down by only assigning a new target once it has stopped moving: ",
        pre(
          """if (!this.drone.isMoving) {
            |  var x = Math.random() * 1800 - 900;
            |  var y = Math.random() * 1800 - 900;
            |  this.drone.moveTo(x, y);
            |}""".stripMargin
        ),
        "Once you have copied this into the ", code("onTick"),
        " function you will have to restart the game again to update your drone's code."
      )
    ),
    Subgoal(
      Seq[TagMod](
        "Much better! Lastly, we want your mothership to automatically hunt down enemy drones. ",
        "You can get a list of all enemy drones close to your drone by writing ", code("this.drone.dronesInSight"), ".",
        "Whenever this list is not empty, move to the position of the first drone in the list. ",
        "(i.e. ", code("this.drone.moveTo(<the enemy drone>.position);"), "). ",
        "Otherwise, just move randomly as before."
      ),
      Some(
        "The new code should now look something like this:<br>" +
          "<pre>" +
          "var enemies = this.drone.dronesInSight;<br>" +
          "if (enemies.length > 0) {<br>" +
          "  this.drone.moveTo(enemies[0].position);<br>" +
          "} else // move randomly as before" +
          "</pre>",
        HintButton)
    ),
    Subgoal(
      Seq[TagMod](
        "The only thing left to do is to actually shoot at enemy drones. ",
        "For this, you can just use the ", code("fireMissilesAt(target)"),
        " method that you already encountered in the previous tutorial. "
      ),
      Some(
        "You just need to add one line of code in the same place where you move toward the enemy: " +
          "<pre>this.drone.fireMissilesAt(&lt;the enemy drone&gt;);</pre>",
        HintButton
      )
    ),
    Subgoal(
      Seq[TagMod](
        "In the next mission, you will learn how to harvest resources and build additional drones. ",
        a(href:="/tutorial/3", "Click here to continue.")
      ),
      Some("Follow me on Twitter <a href=\"https://twitter.com/codecraftgame\">@CodeCraftGame</a> to keep up to date with the latest changes.", HintButton),
      () => false
    )
  ),
  performMothershipSetup = true,
  Seq(DestroyAllEnemies)
)


class TimidDrone extends JSDroneController(null) {
  override def onTick(): Unit = {
    if (util.Random.nextDouble() < 0.05) {
      if (dronesInSight.filter(_.isEnemy).length > 0) {
        moveInDirection(position - dronesInSight.filter(_.isEnemy).head.position)
      } else {
        moveTo(util.Random.nextDouble() * 1800 - 900, util.Random.nextDouble() * 1800 - 900)
      }
    }
  }
}
