package components

import japgolly.scalajs.react.ReactElement
import japgolly.scalajs.react.vdom.all._


object Modal {
  def apply(
    modalID: String,
    modalLabel: String,
    title: String
  )(body: TagMod*)(footer: TagMod*): ReactElement = {
    div(
      `class`:="modal fade",
      id:=modalID,
      tabIndex:="-1",
      role:="dialog",
      aria.labelledby:=modalLabel,
      div(`class`:="modal-dialog", role:="document",
        div(`class`:="modal-content",
          div(`class`:="modal-header",
            a(`type`:="button", `class`:="close", "data-dismiss".reactAttr:="modal", aria.label:="Close",
              span(aria.hidden:="true", "\u00D7") // Unicode Character 'Multiplication Sign'
            ),
            h4(`class`:="modal-title", id:=modalLabel, title)
          ),
          div(`class`:="modal-body",
            body
          ),
          div(`class`:="modal-footer",
            footer
          )
        )
      )
    )
  }
}
