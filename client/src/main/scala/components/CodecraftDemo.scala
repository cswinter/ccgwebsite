package components

import org.scalajs.dom.html

import scala.scalajs.js.annotation.JSExport
import cwinter.codecraft.core.api._

@JSExport
object CodecraftDemo {
  @JSExport
  def start(canvas: html.Canvas): Unit = {
    TheGameMaster.canvas = canvas
    TheGameMaster.runL3vL3()
  }
}
