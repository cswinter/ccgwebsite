package betterviews

import components.TutorialGame

import scalatags.Text.all._

object TutorialView {
  def apply(index: Int, isDev: Boolean): String =
    ReactComponentView(
      s"CodeCraft | Tutorial $index",
      "Tutorial",
      TutorialGame(index),
      isDev,
      script(src:="https://cdnjs.cloudflare.com/ajax/libs/ace/1.2.0/ace.js")
    )
}
