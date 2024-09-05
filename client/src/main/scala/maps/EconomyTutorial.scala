package maps

import cwinter.codecraft.core.api.{TheGameMaster, DroneControllerBase, BluePlayer, DroneSpec}
import cwinter.codecraft.core.game.{DroneCount, Spawn, WorldMap}
import cwinter.codecraft.util.maths.{Rectangle, Vector2}
import japgolly.scalajs.react.vdom.all._


object EconomyTutorial
  extends Tutorial(
    map = WorldMap(
      size = new Rectangle(-1000, 1000, -1000, 1000),
      initialDrones = Seq(
        Spawn(
          DroneSpec(constructors = 4, storageModules = 4, missileBatteries = 2),
          Vector2(100, 100),
          BluePlayer,
          name = Some("mothership"),
          resources = 20
        )
      ),
      resourceClusters = Seq.fill(10)((1, 50))
    ),

    enemyControllers = () => Seq.empty,

    missions = IndexedSeq[Subgoal](
      Subgoal(
        "Welcome to the third CodeCraft tutorial! In this mission you will learn the basics of how to harvest minerals and build new drones.",
        Some("Just click the \"Next\" button.", NextButton)
      ),
      Subgoal(
        Seq[TagMod](
          "We will first instruct our mothership to continually build new drones that collect minerals for us. ",
          "Whenever we build a drone, we must specify the name of the file for the controller that should be ",
          "associated with that drone. ",
          "So put the following code into the 'Mothership' ", code("onTick"),
          " method and create a new 'Harvester' file which will contain the code for our harvester drones. ",
          pre(
            """if (!this.drone.isConstructing) {
              |  var modules = {storageModules: 2};
              |  this.drone.buildDrone('Harvester', modules);
              |}""".stripMargin
          )
        )
      ),
      Subgoal(
        Seq[TagMod](
          "Our mothership now checks on every tick whether it is already building a drone. ",
          "If not, we start the construction of a new drone with the ", code("buildDrone"), " method. ",
          "The first argument to ", code("buildDrone"), " is just the filename of the controller for the new drone. ",
          "The second argument specifies the modules we want the new drone to have, ",
          "in this case two storage modules which will allow the drone to harvest and transport resources."
        )
      ),
      Subgoal(
        Seq[TagMod](
          "We will now program our harvester. The idea is as follows: ",
          "If it is not doing anything else, the harvester will move randomly around the map. ",
          "When it finds a mineral crystal, it will move towards it and harvest it. ",
          "Once all it's storage is filled, it will move back to the mothership and deposit all the resources. "
        )
      ),
      Subgoal(
        Seq[TagMod](
          "For the scouting, we can use code similar to that in the last tutorial: ",
          pre(
            """if (!this.drone.isHarvesting && !this.drone.isMoving) {
              |  var x = Math.random() * 1800 - 900;
              |  var y = Math.random() * 1800 - 900;
              |  this.drone.moveTo(x, y);
              |}""".stripMargin
          ),
          "The only difference is that we have added the condition ", code("!this.drone.isHarvesting"), ". ",
          "Restart the game to see the code in action."
        )
      ),
      Subgoal(
        Seq[TagMod](
          "Now we want to harvest any mineral crystals we come across. ",
          "For this we can make use of another onEvent method, namely ", code("onMineralEntersVision"),
          ", which is called when a mineral crystal comes into the sight radius of the drone.",
          "It is also passed the mineral crystal object that the drone encountered as an argument. ",
          "All we need to do here is to move towards that mineral crystal if the drone still has space for more resources: ",
          pre(
            """onMineralEntersVision: function(mineral) {
              |  if (this.drone.availableStorage > 0) {
              |    this.drone.moveTo(mineral);
              |  }
              |}""".stripMargin
          ),
          "(Note: In JavaScript, object members are comma separated, so you will also have to insert a ",
          code(","), " after the first method.)"
        )
      ),
      Subgoal(
        Seq[TagMod](
          "The next step is to actually harvest the mineral once we have arrived. ",
          "Once again, there is an onEvent method that will be called when that happens, ",
          code("onArrivesAtMineral"), ".",
          "This method also receives the mineral object in question as an argument. ",
          "Add it to your controller with the following code: ",
          pre("this.drone.harvest(mineral);")
        )
      ),
      Subgoal(
        Seq[TagMod](
          "So now our harvester drones will scout the map and harveste resources. ",
          "Of course we want them to return the resources to the mothership at some point. ",
          "For this we need to refine the ", code("onTick"), " method. ",
          "Instead of always instructing the drone to move to a random position when it is idle, ",
          "we only do so if it's storage is not yet full. Otherwise, we send it back to the mothership: ",
          pre(
            """if (this.drone.availableStorage === 0) {
              |  this.drone.moveTo(Game.mothership);
              |} else <move randomly>""".stripMargin
          )
        )
      ),
      Subgoal(
        Seq[TagMod](
          "Now there is just one more piece missing: ",
          "Once we arrive at the mothership, we want to deposit the mineral crystal. ",
          "As you might have guessed already, there is another function that will be called on arriving at a drone. ",
          "It is called ", code("onArrivesAtDrone"), " and takes the ", code("drone"),
          " we arrive at as an argument. ",
          "All you need to do now is to add this function to your controller as well, and give it the following code: ",
          pre(
            """this.drone.giveResourcesTo(drone);""".stripMargin
          )
        )
      ),
      Subgoal(
        Seq[TagMod](
          "Very good! If you managed all the previous steps, all you need to do now is restart once more ",
          "and watch your growing swarm of drones gobble up all the resources on the map."
        )
      ),
      Subgoal(
        Seq[TagMod](
          "Put everything you learned together to build a complete AI in the ", a(href:="/tutorial/4", "final tutorial")
        ),
        successCriterion = () => false
      )
  ),
  performMothershipSetup = true,
  Seq(DroneCount(5))
)

