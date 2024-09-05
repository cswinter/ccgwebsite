package betterviews

import scalatags.Text
import scalatags.Text.all._

object Index {
  def apply(): String = {
    MainNav(
      "CodeCraft",
      "Home",
      onloadScript = """$('[data-toggle="tooltip"]').tooltip()"""
    )()(
      jumbotron,
      div(
        `class` := "container",
        div(
          `class` := "row",
          div(
            `class` := "col-md-8",
            featurePanels,
            actionButtons
          ),
          div(
            `class` := "col-md-4",
            a(
              `class` := "twitter-timeline",
              "data-dnt".attr := true,
              href := "https://twitter.com/CodeCraftGame",
              "data-widget-id".attr := "642616111905771520",
              "Tweets by @CodeCraftGame"
            )
          ),
          // JavaScript for twitter widget
          raw(
            "<script async>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+\"://platform.twitter.com/widgets.js\";fjs.parentNode.insertBefore(js,fjs);}}(document,\"script\",\"twitter-wjs\");</script>")
        )
      )
    )
  }

  val jumbotron =
    div(
      `class` := "jumbotron",
      paddingTop := "30px",
      paddingBottom := "20px",
      div(
        `class` := "container",
        h1("CodeCraft"),
        p(
          "CodeCraft is a real-time strategy game in which you write a program to command an army of virtual drones. " +
            "No matter whether you are a beginner or an expert programmer, refining your skills has never been this much fun!"
        ),
        p
      )
    )

  val featurePanels =
    div(
      `class` := "row",
      div(
        `class` := "col-md-6",
        panel("Easy to Use")(
          "Thanks to the highly regular game mechanics and well designed API, ",
          "writing a fully functional AI only takes a few lines of code!"
        ),
        panel("Rich Visual Feedback")(
          "The minimalist, cyberpunk/sci-fi inspired graphics allow you to easily inspect ",
          "any aspect of the game world at all times."
        ),
        panel("Progressive Difficulty")(
          "The succession of singleplayer levels features several opponents at a wide range of skill levels."
        )
      ),
      div(
        `class` := "col-md-6",
        panel("Strategic Depth")(
          "CodeCraft's game mechanics embody the essence of RTS games and allow for complex strategies."
        ),
        panel("Stimulates Creativity")(
          "Simple win conditions encourage you to explore the vast space of possible strategies ",
          "instead of forcing you to implement very specific solutions."
        ),
        panel("Multiplayer ")(
          "You can play against your friends, and compete with players all around the world."
        )
      )
    )

  def panel(heading: Text.Modifier*)(content: Text.Modifier*) =
    div(
      `class` := "panel panel-default",
      div(`class` := "panel-heading", strong(heading)),
      div(`class` := "panel-body", content)
    )

  def actionButtons =
    div(
      `class` := "row",
      div(
        `class` := "col-md-12",
        textAlign := "center",
        actionButton(
          "WebGL Gameplay",
          "Demo",
          "/demo"
        ),
        actionButton(
          "Scala/Java",
          "Tutorial",
          "https://github.com/cswinter/codecraft-tutorial#readme"
        ),
        actionButton(
          "JavaScript Tutorial",
          "(experimental)",
          "/tutorial"
        )
      )
    )

  def actionButton(line1: String, line2: String, link: String) =
    a(
      `class` := "btn btn-secondary btn-lg",
      `type` := "button",
      marginRight := "10px",
      marginBottom := "10px",
      href := link,
      line1,
      br,
      line2
    )
}
