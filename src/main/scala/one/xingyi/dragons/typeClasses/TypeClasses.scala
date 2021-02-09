package one.xingyi.dragons.typeClasses

trait ToJson[T] {
  def apply(t: T): String
}

object ToJson {
  implicit val toJsonForInt: ToJson[Int] = _.toString
  implicit val toJsonForString: ToJson[String] = '"' + _.toString + '"'
  def apply[T](t: T)(implicit toJson: ToJson[T]): String = toJson(t)
}

case class NameValue(name: String, value: String)

object NameValue {
  implicit val toJsonForNameValue: ToJson[NameValue] = t => s"${t.name}=${t.value}"
}

object UserOfToJson {
  def printJson[T: ToJson](t: T) = println(ToJson(t))

  printJson(2)
  printJson("2")
  printJson(NameValue("someName", "someValue"))
}


