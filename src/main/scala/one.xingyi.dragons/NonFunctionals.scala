package one.xingyi.dragons
import java.util.concurrent.atomic.AtomicInteger

trait LoggerMessage[From, To] extends ((From, To) => String)
object LoggerParams {
  def apply[From, To](from: From, to: To)(implicit loggerMessage: LoggerMessage[From, To]): String = loggerMessage(from, to)
}
trait Logger {
  def message(s: String)
  def error(s: String, e: Exception)
}

trait MetricsName[T] extends (T => String)
object MetricsName {
  def apply[T](t: T)(implicit metricsName: MetricsName[T]) = metricsName(t)
}
trait PutMetrics {
  def addOne[T: MetricsName](t: T)
}
class MapPutMetrics extends PutMetrics {
  var map = Map[String, AtomicInteger]()
  val lock = new Object
  override def addOne[T: MetricsName](t: T): Unit = {
    val name = MetricsName(t)
    if (!map.contains(name)) lock.synchronized(if (!map.contains(name)) map = map + (name -> new AtomicInteger()))
    map(name).incrementAndGet()
  }
}

object PrintlnLogger extends Logger {
  override def error(s: String, e: Exception): Unit = {println(s); e.printStackTrace() }
  override def message(s: String): Unit = println(s)
}

trait ErrorStrategy[From, To] {
  def apply(from: From, e: Exception): To
}
object ErrorStrategy {
  def justThrow[From, To]: ErrorStrategy[From, To] = (f, e) => throw e
  implicit def defaultJustThrow[From, To]: ErrorStrategy[From, To] = justThrow
}


trait NonFunctionals[From, To] extends ((From => To) => (From => To))
object NonFunctionals {
  type Delegate[From, To] = (From => To) => (From => To)

  def doWithSideeffect[From, To](sideeffect: (From, To) => Unit): Delegate[From, To] = //
  { raw => from => val to = raw(from); sideeffect(from, to); to }

  def logging[From, To](implicit logger: Logger, loggerMessage: LoggerMessage[From, To]): Delegate[From, To] =
    doWithSideeffect[From, To]((from, to) => logger.message(loggerMessage(from, to)))

  def metrics[From, To: MetricsName](implicit putMetrics: PutMetrics): Delegate[From, To] =
    doWithSideeffect[From, To]((from, to) => putMetrics.addOne(to))

  def error[From, To](implicit errorStrategy: ErrorStrategy[From, To]): Delegate[From, To] =
    raw => from => try {raw(from) } catch {case e: Exception => errorStrategy(from, e)}

  def compose[From, To](fns: Delegate[From, To]*): NonFunctionals[From, To] =
    raw => fns.foldLeft(raw)((acc, fn) => fn(acc))

  implicit def nonFunctionals[From, To: MetricsName](implicit putMetrics: PutMetrics, loggerMessage: LoggerMessage[From, To], logger: Logger, errorStrategy: ErrorStrategy[From, To]): NonFunctionals[From, To] =
    compose[From, To](logging, metrics, error)

}

