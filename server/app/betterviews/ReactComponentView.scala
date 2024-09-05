package betterviews

import scalatags.Text.all._
import components.ReactComponent

object ReactComponentView {
  def apply(
      pageTitle: String,
      activeNav: String,
      reactBaseComponent: ReactComponent,
      isDev: Boolean,
      additionalHTML: Frag*
  ): String =
    MainNav(
      pageTitle,
      activeNav,
      "components.ReactInstaller().install()",
      if (isDev) MainNav.FastOptScalaJS else MainNav.FullOptScalaJS
    )()(
      additionalHTML,
      div(
        id := "react-container",
        `class` := "fill",
        data("component-json") := reactBaseComponent.serialize
      )
  )
}
