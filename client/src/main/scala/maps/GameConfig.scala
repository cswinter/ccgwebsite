package maps

import components._
import cwinter.codecraft.core.api.JSDroneController
import cwinter.codecraft.core.game.{DroneWorldSimulator, WorldMap}

import scala.scalajs.js

trait GameConfig {
  def init(): DroneWorldSimulator


  def createController(provider: JSControllerProvider): JSDroneController = {
    new JSDroneController(
      provider.createController,
      _errorHandler = Some(CodecraftJS.exceptionLogger _)
    )
  }

  def tryInitialiseMothership(mothership: JSDroneController, provider: JSControllerProvider): Unit = {
    mothership.updateController(provider.createNativeController("Mothership"), "Mothership")
    js.Dynamic.global.Game.mothership = mothership.asInstanceOf[js.Any]
  }

  def initialiseMothershipOrEmitWarning(mothership: JSDroneController, provider: JSControllerProvider): Unit = {
    try {
      tryInitialiseMothership(mothership, provider)
    } catch {
      case e: Throwable =>
        JSConsole.logError.foreach(logger =>
          logger(s"Could not assign initial 'Mothership' controller:\n $e}")
        )
        CodecraftJS.MothershipInitAlert.show(3000)
    }
  }

  def createIsolatedController(provider: JSControllerProvider): JSDroneController = {
    val mothership = createController(provider)
    val environmentSetup = () => js.Dynamic.global.Game.mothership = mothership.asInstanceOf[js.Any]
    provider.preEventProcessingHook = Some(environmentSetup)
    mothership.setPreEventProcessingHook(environmentSetup)
    mothership
  }


  def provider = JSEditor.droneControllerProviderProvider()
}

