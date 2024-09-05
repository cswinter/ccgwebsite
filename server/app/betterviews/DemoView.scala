package betterviews

import components.GameplayDemo

object DemoView {
  def apply(isDev: Boolean): String =
    ReactComponentView(
      s"CodeCraft | Demo",
      "Demo",
      GameplayDemo,
      isDev
    )
}

