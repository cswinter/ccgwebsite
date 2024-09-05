package components

import components.JSEditor.State
import cwinter.codecraft.core.api.{TheGameMaster, GameConstants, JSDroneController}
import japgolly.scalajs.react.vdom.all.{s => _, _}
import japgolly.scalajs.react._
import org.scalajs.dom.raw.{HTMLLabelElement, KeyboardEvent}
import org.scalajs.dom.{document, window}

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.JavaScriptException
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.JSConverters._


@JSExport
object JSEditor {
  case class Props(
    fileTemplate: String
  )

  case class State(
    filenames: Seq[String] = Seq("Mothership"),
    openFile: Option[String] = Some("Mothership")
  )

  val NoWebStorageAlert = new AlertWarning(
    "no-web-storage-alert",
    strong("Warning: "),
    "Your browser does not seem to support web storage. ",
    "Any changes you make will be lost once you close the website."
  )

  val OldCodeVersionAlert = new AlertWarning(
    "old-code-version-alert",
    strong("Warning: "),
    "There have been breaking changes to the API or game mechanics since you last used CodeCraft. ",
    "You may have to slightly modify your code to get it working again. "
  )

  val C = ReactComponentB[Props]("JSEditor")
    .initialState(State())
    .renderBackend[Backend]
    .componentDidMount(_.backend.start)
    .componentWillUnmount(_.backend.stop)
    .componentWillUpdate(_.component.backend.componentWillUpdate)
    .componentDidUpdate(_.component.backend.componentDidUpdate)
    .build


  var droneControllerProviderProvider: () => JSControllerProvider = null
  class Backend(
    val self: BackendScope[Props, State]
  ) {
    val fileManager = new FileManager(self.props.runNow().fileTemplate)
    def fileLabel = document.getElementById("file-label").asInstanceOf[HTMLLabelElement]


    def render(state: State) =
      div(`class`:="full-height",
        div(`class`:="container-fluid editor-buttons",
          div(`class`:="row",
            div(`class`:="col-xs-5 editor-filename",
              label(
                id:="file-label",
                state.openFile,
                contentEditable:=true,
                onKeyDown ==> fileNameKeyDown,
                onBlur --> fileNameBlur
              ),
              label(
                ".js",
                onClick --> Callback {
                  document.getElementById("file-label").asInstanceOf[HTMLLabelElement].focus()
                })
              ),
              div(`class`:="col-xs-2",
                button(`type`:="button", `class`:="btn btn-danger editor-button",
                  onClick --> deleteFile,
                  "Delete"
                )
              ),
              div(`class`:="col-xs-2",
                button(`type`:="button", `class`:="btn btn-primary pull-left editor-button",
                  onClick --> newFile,
                  "New"
                )
              ),
              div(`class`:="col-xs-3",
                div(`class`:="dropdown",
                  button(
                    `class`:="btn btn-default dropdown-toggle editor-button",
                    `type`:="button",
                    id:="dropdownOpenFile",
                    "data-toggle".reactAttr:="dropdown",
                    aria.haspopup:=true,
                    aria.expanded:=true,
                    "Open",
                    span(`class`:="caret")
                  ),
                  ul(`class`:="dropdown-menu dropdown-menu-right", aria.labelledby:="dropdownOpenFile",
                    for (filename <- state.filenames)
                      yield
                        li(
                          a(
                            href:="#",
                            if (filename == "") "UNNAMED"
                            else filename, onClick --> openFile(filename)
                          )
                        )
                  )
                )
              )
            )
          ),
          div(`class`:="relative editor-container",
            state.openFile.fold[TagMod](
              "Create a new file by clicking the \"New\" button."
            )(filename =>
              div(id:="editor")
            )
          )
        )


    def start = {
      val initFiles = Callback { fileManager.initialise(); self.setState(fileManager.state).runNow() }
      val initDom = Callback {
        g.$("a[href=#editor-pane]").click((_: js.Any) => g.ace.edit("editor").focus())
        g.AceRange = g.require("ace/range").Range
        if (js.typeOf(g.Game) == "undefined") g.Game = new js.Object
        g.Game.assignController = assignController _
        g.Game.getController = getController _
        droneControllerProviderProvider = () => JSControllerProvider(fileManager.allFiles)
      }

      initFiles >> initDom >> initialiseEditor
    }

    def assignController(drone: JSDroneController, controller: String): Unit = {
      val nativeController = evalController(controller)
      drone.updateController(nativeController, controller)
      nativeController.drone = drone.asInstanceOf[js.Any]
    }

    def getController(name: String): JSDroneController = {
      val controller =
        new JSDroneController(
          getController,
          _errorHandler = Some(CodecraftJS.exceptionLogger),
          _nativeControllerName = name
        )

      val nativeController = try {
        evalController(name)
      } catch {
        case e: JavaScriptException =>
          throw new JavaScriptException(s"Cannot get controller for '$name': ${e.toString}")
      }
      controller.updateController(nativeController, name)
      nativeController.drone = controller.asInstanceOf[js.Any]
      controller
    }

    private def evalController(name: String): js.Dynamic = {
      fileManager.contents(name) match {
        case Some(text) =>
          js.eval(text)
          g._droneController
        case None => throw new IllegalArgumentException(s"There is no drone controller called '$name'")
      }
    }

    def stop = Callback {
      val editor = js.Dynamic.global.ace.edit("editor")
      editor.destroy()
    }

    def componentWillUpdate = self.state.map[Unit] { s =>
      if (s.openFile.nonEmpty) {
        val editor = js.Dynamic.global.ace.edit("editor")
        editor.destroy()
      }
    }

    def newFile = Callback {
      fileManager.addFile()
      self.setState(fileManager.state).runNow()
      fileLabel.focus()
      selectElementContents(fileLabel)
    }

    def deleteFile = self.state.map { state =>
      state.openFile.foreach(file => {
        fileManager.delete(file)
        self.setState(fileManager.state).runNow()
      })
    }

    def fileNameKeyDown(e: KeyboardEvent) = Callback {
      if (e.keyCode == 13) {
        e.preventDefault()
        js.Dynamic.global.ace.edit("editor").focus()
      }
    }

    def fileNameBlur = renameFile(fileLabel.textContent)

    def renameFile(name: String) = self.state.map[Unit] { state =>
      val newName = if (name == "") "Unnamed" else name
      if (state.openFile.nonEmpty && state.openFile != Some(newName)) {
        fileManager.rename(state.openFile.get, newName)
        self.setState(fileManager.state).runNow()

        js.Dynamic.global.ace.edit("editor").focus()
      }
    }

    def componentDidUpdate = initialiseEditor

    def initialiseEditor = self.state.map { state =>
      state.openFile.foreach(name => {
        val editor = js.Dynamic.global.ace.edit("editor")
        editor.$blockScrolling = "Infinity"
        editor.setTheme("ace/theme/monokai")
        val session = editor.getSession()
        session.setMode("ace/mode/javascript")
        Ace.restoreFromState(session, fileManager.files(name))
        editor.on("change", editorChange _)
        if (fileLabel != document.activeElement) js.Dynamic.global.ace.edit("editor").focus()
      })
    }

    def editorChange(reactEventI: ReactEventI): Unit = {
      fileManager.persistChanges()
    }

    def openFile(filename: String) = Callback {
      fileManager.open(filename)
      self.setState(fileManager.state).runNow()
    }


    def selectElementContents(el: HTMLLabelElement): Unit = {
      val range = document.createRange()
      range.selectNodeContents(el)
      val sel = window.getSelection()
      sel.removeAllRanges()
      sel.addRange(range)
    }
  }

  def apply(
    fileTemplate: String =
      """
        |var _droneController = {
        |  onTick: function() {
        |  }
        |};
        |""".stripMargin
  ) = C(Props(fileTemplate))
}

class FileManager(val newFileTemplate: String) {
  var files: Map[String, js.Dynamic] = Map(
    "Mothership" ->
      Ace.createEditorSessionState(
        """// After making changes, click the 'Restart' button
          |// to restart the game and update the code
          |
          |var _droneController = {
          |  // this function will be called on every timestep
          |  onTick: function() {
          |    // your code goes here
          |    // you can access your drone via this.drone
          |  }
          |};
          |""".stripMargin
      )
  )
  var openFile: Option[String] = Some("Mothership")

  def initialise(): Unit = {
    if (localStorageAvailable) {
      val lastVersion = window.localStorage.getItem("javascript-api-version")
      if (lastVersion != TheGameMaster.JavascriptAPIVersion)
        JSEditor.OldCodeVersionAlert.showPermanently()

      val fileList = js.JSON.parse(window.localStorage.getItem("filenames"))
      if (fileList != null && fileList.isInstanceOf[js.Array[_]]) {
        val filenames = fileList.asInstanceOf[js.Array[String]].toArray.sorted
        files = {
          for {
            name <- filenames
            session = storageRetrieve(name)
          } yield (name, js.JSON.parse(session))
        }.toMap
        openFile =
          if (filenames.contains("Mothership")) Some("Mothership")
          else filenames.headOption
      }
    } else {
      JSEditor.NoWebStorageAlert.showPermanently()
      println("can't local storage")
    }
  }

  def allFiles: Map[String, String] = files.map {
    case (filename, file) => (filename, file.value.toString)
  }

  def contents(file: String): Option[String] = files.get(file).map(_.value.toString)

  def addFile(): Unit = {
    persistChanges()

    val name = getUniqueName("")
    files += name -> Ace.createEditorSessionState(newFileTemplate)
    openFile = Some(name)
    persistChanges()
  }

  def delete(file: String): Unit = {
    val filesNew = files - file
    if (localStorageAvailable) {
      storageRemove(file)
      window.localStorage.setItem("filenames", js.JSON.stringify(filesNew.keys.toJSArray))
    }

    files = filesNew
    openFile = files.keys.headOption
  }

  def rename(oldName: String, newName: String): Unit = {
    val newUniqueName = getUniqueName(newName)
    val newFiles = files + (newUniqueName -> files(oldName)) - oldName

    if (localStorageAvailable) {
      storageSet(newUniqueName, storageRetrieve(oldName))
      storageRemove(oldName)
      window.localStorage.setItem("filenames", js.JSON.stringify(newFiles.keys.toJSArray))
    }

    files = newFiles
    if (openFile == Some(oldName)) openFile = Some(newUniqueName)
  }

  def open(name: String): Unit = {
    persistChanges()
    openFile = Some(name)
  }

  def state = State(files.keys.toSeq.sorted, openFile)

  def persistChanges(): Unit = {
    openFile.foreach(name => {
      val editor = g.ace.edit("editor")
      val editorState = Ace.extractEditorState(editor.getSession())
      if (localStorageAvailable) {
        window.localStorage.setItem("javascript-api-version", TheGameMaster.JavascriptAPIVersion)
        window.localStorage.setItem("filenames", js.JSON.stringify(files.keys.toJSArray))
        storageSet(name, js.JSON.stringify(editorState))
      }
      files = files.updated(name, editorState)
    })
  }

  private def localStorageAvailable: Boolean = try {
    val storage = window.localStorage
    val x = "__storage_test__"
    storage.setItem(x, x)
    val result = storage.getItem(x)
    result == x
  } catch {
    case e: Throwable => false
  }

  private def storageRetrieve(file: String) = window.localStorage.getItem("file:" + file)
  private def storageRemove(file: String): Unit = window.localStorage.removeItem("file:" + file)
  private def storageSet(file: String, contents: String): Unit = window.localStorage.setItem("file:" + file, contents)

  def getUniqueName(prefix: String): String = {
    var freshName = prefix
    var postfix = 1
    while (files.contains(freshName)) {
      freshName = prefix + postfix
      postfix += 1
    }
    freshName
  }
}

object Ace {
  // http://stackoverflow.com/questions/20395991/is-it-possible-to-serialize-an-ace-session-object
  def extractEditorState(session: js.Dynamic): js.Dynamic = {
    val state = js.Dynamic.literal(
      value = session.getValue(),
      selection = session.selection.toJSON(),
      options = session.getOptions(),
      folds = session.getAllFolds().map((fold: js.Dynamic) => {
        js.Dynamic.literal(
          start = fold.start,
          end = fold.end,
          placeholder = fold.placeholder
        )
      }),
      scrollTop = session.getScrollTop(),
      scrollLeft = session.getScrollLeft()
    )
    state
  }

  def restoreFromState(session: js.Dynamic, state: js.Dynamic): Unit = {
    session.setValue(state.value)
    session.selection.fromJSON(state.selection)
    session.setOptions(state.options)
    try {
      state.folds.forEach((fold: js.Dynamic) =>
        session.addFold(fold.placeholder, g.AceRange.fromPoints(fold.start, fold.end))
      )
    } catch { case e: Throwable =>  } // whatever
    session.setScrollTop(state.scrollTop)
    session.setScrollTop(state.scrollLeft)
  }

  def createEditorSessionState(fileContents: String): js.Dynamic = {
    js.Dynamic.literal(
      value = fileContents,
      selection = js.Dynamic.literal(
        start = js.Dynamic.literal(row = 0, column = 0),
        end = js.Dynamic.literal(row = 0, column = 0),
        isBackwards = false
      ),
      options = js.Dynamic.literal(
        tabSize = 2,
        useSoftTabs = true
      ),
      folds = new js.Array(),
      scrollTop = 0,
      scrollLeft = 0
    )
  }
}