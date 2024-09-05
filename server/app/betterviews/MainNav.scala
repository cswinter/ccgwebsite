package betterviews

import scalatags.Text.all._
import scalatags.Text.tags2.{title => titleTag, nav}

object MainNav {
  final val `data-toggle` = "data-toggle".attr
  final val `data-target` = "data-target".attr
  final val `aria-expanded` = "aria-expanded".attr
  final val `aria-controls` = "aria-controls".attr
  final val `aria-haspopup` = "aria-haspopup".attr

  final val tabs = Seq[Either[(String, String), (String, Seq[(String, String)])]](
    Left(("/", "Home")),
    Left(("/docs", "Documentation")),
    Left("/demo", "Demo"),
    Right(
      "JavaScript (experimental)",
      Seq(
        ("/tutorial", "Tutorial"),
        ("/singleplayer", "Singleplayer")
      )
    )
  )

  def apply(
    titleString: String,
    activeNav: String,
    onloadScript: String = "",
    jsAssets: JSAssets = NoJSAssets
  )(headerContent: Frag*)(content: Frag*): String = {
    "<!DOCTYPE html>" +
      html(
        head(
          meta(charset := "utf-8"),
          meta(httpEquiv := "X-UA-Compatible", scalatags.Text.all.content := "IE=edge"),
          meta(name := "viewport",
               scalatags.Text.all.content := "width=device-width",
               "initial-scale".attr := 1),
          titleTag(titleString),
          // bootstrap
          link(rel := "stylesheet",
               href := "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css"),
          //link(rel:="stylesheet", href:="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css"),
          link(rel := "stylesheet", media := "screen", href := "/assets/stylesheets/main.css"),
          link(rel := "shortcut icon", tpe := "image/png", href := "/assets/images/favicon.png"),
          headerContent,
          /* HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries */
          raw("""
          <!--[if lt IE 9]>
          <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
          <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
          <![endif]-->
        """),
          /* Google analytics */
          raw("""
        <script>
          (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
          (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
        m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
        })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

        ga('create', 'UA-66549646-2', 'auto');
        ga('send', 'pageview');
        </script>
        """)
        ),
        body(
          nav(
            `class` := "navbar navbar-inverse navbar-fixed-top",
            div(
              `class` := "container",
              div(
                `class` := "navbar-header",
                button(
                  `type` := "button",
                  `class` := "navbar-toggle collapsed",
                  `data-toggle` := "collapse",
                  `data-target` := "#navbar",
                  `aria-expanded` := "false",
                  `aria-controls` := "navbar",
                  span(`class` := "sr-only", "Toggle navigation"),
                  span(`class` := "icon-bar"),
                  span(`class` := "icon-bar"),
                  span(`class` := "icon-bar")
                ),
                a(`class` := "navbar-brand", href := "/", "CodeCraft")
              ),
              div(
                id := "navbar",
                `class` := "collapse navbar-collapse",
                ul(
                  `class` := "nav navbar-nav",
                  for (tab <- tabs)
                    yield
                      tab match {
                        case Left((url, name)) =>
                          li(
                            a(href := url, name),
                            if (activeNav == name) `class` := "active" else Seq[Frag]()
                          )
                        case Right((ddname, elems)) =>
                          li(
                            if (elems.exists(_._2 == activeNav)) `class` := "dropdown active"
                            else `class` := "dropdown",
                            a(
                              href := "#",
                              `class` := "dropdown-toggle",
                              `data-toggle` := s"dropdown",
                              role := "button",
                              `aria-haspopup` := true,
                              `aria-expanded` := false,
                              ddname,
                              span(`class` := "caret")
                            ),
                            ul(`class` := "dropdown-menu", for ((url, name) <- elems) yield {
                              li(
                                a(href := url, name),
                                if (activeNav == name) `class` := "active" else Seq[Frag]()
                              )
                            })
                          )
                      }
                )
              )
            )
          ),
          content,
          // jquery
          script(src := "https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"),
          // bootstrap javascript
          script(src := "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"),
          jsAssets match {
            case NoJSAssets => ()
            case FastOptScalaJS =>
              List(scriptAssetLink("client-jsdeps.js"), scriptAssetLink("client-fastopt.js"))
            case FullOptScalaJS =>
              List(scriptAssetLink("client-jsdeps.js"), scriptAssetLink("client-opt.js"))
          },
          onload := onloadScript
        )
      ).render
  }

  private def scriptAssetLink(filename: String): Frag = script(
    src := s"/assets/$filename",
    tpe := "text/javascript"
  )

  sealed trait JSAssets
  case object NoJSAssets extends JSAssets
  case object FastOptScalaJS extends JSAssets
  case object FullOptScalaJS extends JSAssets
}
