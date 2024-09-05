package betterviews

import scalatags.Text.all._

object DocumentationView {
  def apply(): String = {
    MainNav("CodeCraft | Documentation", "Documentation")()(
      div(
        `class` := "fixed-width",
        h2("Getting Started"),
        p(
          strong("Scala/Java"),
          ": ",
          a("This tutorial on GitHub", href := "https://github.com/cswinter/codecraft-tutorial#readme"),
          " will have you up and running in no time!"
        ),
        p(
          strong("JavaScript"),
          " (experimental):",
          "Check out the ",
          a(href := "/tutorial", "interactive tutorial"),
          ", which will run right in your browser."
        ),
        h2("Getting Help"),
        p(
          "If you have any questions, you can get an answer by posting to ",
          a(href := "https://gitter.im/cswinter/CodeCraftGame", "Gitter cswinter/CodeCraftGame"),
          "."
        ),
        h2("Documentation"),
        p(
          "For a comprehensive documentation of the API, see the ",
          a(href := "docs/api/index.html", "API reference"),
          ". "
        )
      )
    )
  }
}
