package betterviews

import scalatags.Text.all._

object SingleplayerSelectionView {
  def apply(): String = {
    MainNav("CodeCraft | Singleplayer", "Singleplayer")()(
      div(`class` := "fixed-width",
          SelectionView(
            for (i <- 1 to 7) yield {
              SelectionItem(
                heading = s"Level $i",
                description = "",
                url = s"/singleplayer/$i"
              )
            },
            100
          ))
    )
  }
}
