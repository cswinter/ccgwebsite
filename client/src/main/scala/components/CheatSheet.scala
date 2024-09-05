package components

import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.all._


object CheatSheet {
  val C = ReactComponentB[Unit]("CheatSheet")
    .render(x =>
      div(
        p(
          "A quick reference for the most important parts of the API. The complete documentation can be found ",
          a(href:="/docs", "here"), "."
        ),
        strong("The ", code("Game"), " object"),
        ul(
          li(
            code("Game.mothership"), " - ", em("the object representing your mothership.")
          ),
          li(
            code("Game.vector2(x, y)"), " - ",
            em(
              "creates an ", doclink("cwinter.codecraft.util.maths.Vector2", "immutable 2D vector")
            )
          ),
          li(
            code("Game.spec(modules)"), " - ",
            em("creates a ", code("DroneSpec"), " object that exposes ",
              doclink("cwinter.codecraft.core.api.DroneSpec", "various properties"),
              " about drones with the specified modules, such as the amount of resources required for construction."
            )
          )
        ),

        section("Event handlers")(
          "Automatically called by the game, add these to your controller ",
          "to execute code whenever some event takes place."
        )(
          "onSpawn()",
          "onDeath()",
          "onTick()",
          "onMineralEntersVision(mineral)",
          "onDroneEntersVision(drone)",
          "onArrivesAtPosition()",
          "onArrivesAtMineral(mineral)",
          "onArrivesAtDrone(drone)"
        ),

        section("Commands")(
          "Methods on ", code("this.drone"), " that issue commands."
        )(
          "moveInDirection(direction)",
          "moveTo(drone|mineral|position)",
          "harvest(mineral)",
          "giveResourcesTo(drone)",
          "fireMissilesAt(drone)",
          "buildDrone(controller, modules)",
          "drawText(text: String)"
        ),
        "Example for ", code("modules"), " parameter on ", code("buildDrone"), ":",
        code("{storageModules: 2, engines: 2}"),
        br,
        "Possible options are: ",
        code("storageModules, missileBatteries, constructors, engines, shieldGenerators"),
        br,
        br,

        section("Properties")(
          "Drones expose the following properties ", doclink("cwinter.codecraft.core.api.Drone", "and more."),
          " For enemy drones, any non-constant properties can only be accessed while they are visible to one of your drones."
        )(
          "position: Vector2",
          "orientation: Double",
          "isEnemy: Boolean",
          "isVisible: Boolean",
          "isConstructing: Boolean",
          "isMoving: Boolean",
          "isHarvesting: Boolean",
          "hitpoints: Int",
          "availableStorage: Int",
          "storedResources: Int",
          "dronesInSight: Array[Drone]",
          "worldSize: Rectangle",
          "lastKnownPosition: Vector2",
          "lastKnownOrientation: Vector2",
          "weaponsCooldown: Int",
          "spec: DroneSpec",
          "playerID: Int",
          "isInMissileRange(drone): Boolean"
        )
      )
    )
    .buildU

  private def doclink(classpath: String, linktext: String) =
    a(href:=s"/docs/api/index.html#$classpath", linktext)


  private def section(heading: TagMod*)(text: TagMod*)(methods: String*) = {
    Seq(
      strong(heading),
      p(text),
      ul(
        for (a <- methods)
          yield li(code(a))
      )
    )
  }
}
