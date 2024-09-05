package betterviews

import scalatags.text.Frag
import scalatags.Text.all._

case class SelectionItem(
  heading: String,
  description: String,
  url: String
)

object SelectionView {
  def apply(items: Seq[SelectionItem], itemWidth: Int): Frag = {
    div(`class` := "list-group", width := s"${itemWidth}px",
      for (SelectionItem(heading, description, url) <- items) yield {
        a(href := url, `class` := "list-group-item",
          h4(`class` := "list-group-item-heading", heading),
          p(`class` := "list-group-item-text", description)
        )
      }
    )
  }
}

