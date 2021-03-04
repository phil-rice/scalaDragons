package one.xingyi.dragons
trait LogPrinter {
  def apply(s: String)
}
object PrintlnLogPrinter extends LogPrinter {
  override def apply(s: String): Unit = println(s)
}

trait PutMetrics {
  def apply(name: String)
}

object NonFunctionals {
  def addError[From, To](handler: (From, Exception) => To)(raw: From => To): From => To =
    from => try {raw(from) } catch {case e: Exception => handler(from, e)}

  def addLogging[From, To](logPrinter: LogPrinter, logMsgFn: (From, To) => String)(raw: From => To): From => To = {
    from =>
      val result = raw(from)
      logPrinter(logMsgFn(from, result))
      result
  }
  def addMetrics[From, To](putMetrics: PutMetrics, metricsNameFunction: (From, To) => String)(raw: From => To): From => To = {
    from =>
      val result = raw(from)
      putMetrics(metricsNameFunction(from, result))
      result
  }

  def compose[From, To](wrappers: (From => To) => (From => To)*): (From => To) => (From => To) =
    raw => wrappers.foldLeft(raw)((acc, fn) => fn(acc))

}
