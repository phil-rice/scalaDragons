package one.xingyi.dragons

trait FailedValidation[From, To] {
  def apply(from: From, list: List[String]): To
}

trait Validation[T] extends (T => List[String])

object Validation {
  def apply[T](require: T => Boolean, stringIfNot: String): Validation[T] = t =>
    if (require(t)) Nil else List(stringIfNot)

  def compose[T](validators: Validation[T]*): Validation[T] = t => validators.toList.flatMap(v => v(t))
}



