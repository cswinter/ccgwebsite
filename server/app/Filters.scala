import java.io.{PrintWriter, StringWriter}
import javax.inject.Inject

import akka.stream.Materializer
import org.joda.time.format.DateTimeFormat
import play.api.{Environment, Logger}
import play.api.http.DefaultHttpFilters
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class Filters @Inject()(
  accessLog: AccessLoggingFilter
) extends DefaultHttpFilters(accessLog)

class AccessLoggingFilter @Inject()(implicit val mat: Materializer, env: Environment) extends Filter {

  val accessLogger = Logger("access")
  val dateFormat = DateTimeFormat.forPattern("dd.MM.YYYY HH:mm:ss.SSS")

  def apply(next: (RequestHeader) => Future[Result])(request: RequestHeader): Future[Result] = {
    val resultFuture = next(request)
    if (isHealthcheckRequest(request)) return resultFuture

    val infoMsg = s"method=${request.method} uri=${request.uri} remote-address=${request.remoteAddress}"
    resultFuture.onComplete {
      case Success(result) => accessLogger.info(infoMsg + s" status=${result.header.status}")
      case Failure(e) =>
        accessLogger.error(infoMsg + s" error-message=${'"' + e.getMessage}")
        val stackTrace = new PrintWriter(new StringWriter)
        e.printStackTrace(stackTrace)
    }

    resultFuture
  }

  private def isHealthcheckRequest(request: RequestHeader): Boolean =
    request.queryString.get("source").exists(_.contains("healthcheck"))
}
