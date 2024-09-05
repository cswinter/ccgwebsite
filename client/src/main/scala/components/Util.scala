package components

import japgolly.scalajs.react.{Callback, ReactKeyboardEventI}


object Util {
  def performOnEnter(callback: Callback)(event: ReactKeyboardEventI) = Callback {
    if (event.nativeEvent.keyCode == 13) {
      event.preventDefault()
      callback.runNow()
    }
  }
}

