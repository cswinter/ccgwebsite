package betterviews

import components.SingleplayerGame

import scalatags.Text.all._

object SingleplayerView {
  def apply(index: Int, isDev: Boolean): String =
    ReactComponentView(
      s"CodeCraft | Level $index",
      "Singleplayer",
      SingleplayerGame(index),
      isDev,
      script(src:="https://cdnjs.cloudflare.com/ajax/libs/ace/1.2.0/ace.js")
    )
}
