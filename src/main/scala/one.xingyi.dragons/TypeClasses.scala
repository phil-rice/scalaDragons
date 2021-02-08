package one.xingyi.dragons

case class NameValue(name: String, value: String)

object NameValue {
  implicit object LoggingSummaryForNameValue extends LoggingSummary[NameValue] {
    override def apply(t: NameValue): String = s"${t.name}=${t.value}"
  }
}

trait LoggingSummary[T] {
  def apply(t: T): String
}
object LoggingSummary {
  implicit def defaultLoggingSummary[T]: LoggingSummary[T] = _.toString
  def apply[T](t: T)(implicit loggingSummary: LoggingSummary[T]): String = loggingSummary(t)
}

