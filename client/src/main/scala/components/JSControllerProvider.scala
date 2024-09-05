package components

import cwinter.codecraft.core.api.JSDroneController

import scala.scalajs.js
import scala.scalajs.js.JavaScriptException
import scala.scalajs.js.Dynamic.global


case class JSControllerProvider(
  files: Map[String, String]
) {
  def createController(name: String): JSDroneController = {
    val controller = new JSDroneController(
      createController,
      _errorHandler = Some(CodecraftJS.exceptionLogger),
      _nativeControllerName = name
    )
    for (hook <- preEventProcessingHook)
      controller.setPreEventProcessingHook(hook)

    if (!files.contains(name)) {
      throw new IllegalArgumentException(s"There is no controller file with name '$name'.")
    } else {
      try {
        val nativeController = createNativeController(name)
        controller.updateController(nativeController, name)
        nativeController.drone = controller.asInstanceOf[js.Any]
        controller
      } catch {
        case e: JavaScriptException =>
          throw new JavaScriptException(s"Cannot get controller for '$name': ${e.toString}")
      }
    }
  }


  def createNativeController(name: String): js.Dynamic = {
    js.eval(files(name))
    global._droneController
  }


  var preEventProcessingHook: Option[() => Unit] = None
}

