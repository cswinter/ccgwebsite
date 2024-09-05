package betterviews

import scalatags.Text.all._


object TutorialSelectionView {
  def apply(): String = {
    MainNav("CodeCraft | Tutorials", "Tutorial")()(
      div(`class` := "fixed-width",
        SelectionView(
          Seq(
            SelectionItem(
              heading = "First steps",
              description = "Learn how to give basic commands to your drones using the console.",
              url = "/tutorial/1"
            ),
            SelectionItem(
              heading = "Automation",
              description = "Learn the basics of automating your drones.",
              url = "/tutorial/2"
            ),
            SelectionItem(
              heading = "Economy",
              description = "Learn how to harvest resources and build new drones.",
              url = "/tutorial/3"
            ),
            SelectionItem(
              heading = "Putting it all together",
              description = "Build a fully functional AI in the final tutorial mission.",
              url = "/tutorial/4"
            )
          ),
          450
        )
      )
    )
  }
}
