package controllers


import play.api.mvc._
import java.net.URL


/**

 */
object Application extends Controller {
  import scalaz._
  import Scalaz._

  type Error = String

  val S = Shortener

  def url: String => \/[Error, URL] = x => // ahh left-biased Either! :)
    \/.fromTryCatch(new URL(x)).leftMap( _.getMessage )

  def index: Action[AnyContent] = Action {
    Ok(views.html.index("Your new application is ready.")(None))
  }

  def shorten(urlStr:String): Action[AnyContent] = Action {
    val u = url(urlStr)
    u.fold(
      err => Ok(views.html.index("Your new application is ready.")(s"Invalid URL: $err".some)),
      u => Ok(views.html.shortened(u, S.shorten(u)))
    )
  }

  def go(i:Shortener.Index) = Action {
    S.unshorten(i) map { u =>
      Redirect(u.toString)
      //Ok(views.html.shortened(u, S.shorten(u)))
    } getOrElse { Ok(views.html.index("Your new application is ready.")(s"Invalid Index: $i".some)) }
  }

}

/**
 * A very simple blocking BiMap-like structure. Seems good enough for a exercise.
 * Assumes reads are considerably more frequent than writes, therefore the method `shorten`
 * shouldn't be a serious bottleneck.
 */
object Shortener {

  import collection.immutable.Map

  type Index = Long

  val baseUrl = "http://localhost:9000/go/"
  private val lock = new AnyRef

  @volatile private var short2Url = Map[Index, URL]()
  private var url2Short = Map[URL, Index]()
  private var index: Index = 0L

  def shortened(i:Index):URL = new URL(baseUrl + i)

  /* The only place the writes to the maps. */
  def shorten(url:URL): URL = lock.synchronized { // All these mutations annoys me
    val currIndex = index
    url2Short.get(url) map { shortened } getOrElse { // Writes only occurs here. Notice, it's necessary to acquire
      short2Url += (currIndex -> url)                // the lock even for reading url2Short, otherwise the same url
      url2Short += (url -> currIndex)                // not present in url2Short could be add twice. Probably not a big
      index += 1                                     // problem but feels sloppy.
      shortened(currIndex)
    }
  }

  def unshorten(i:Index): Option[URL] = short2Url.get(i)

}
