package one.xingyi.dragons

trait Validation[From] {
  def apply(from: From): List[String]
}

object Validation {
  def apply[From](condition: From => Boolean, s: String): Validation[From] =
    from => if (condition(from)) List() else List(s)
  def compose[From](vs: Validation[From]*): Validation[From] =
    from => vs.foldLeft(List[String]())((acc, v) => acc ::: v(from))
  def fold[From, To](validation: Validation[From])(onFail: (From, List[String]) => To, fn: From => To): From => To = { from =>
    val errors = validation(from)
    if (errors.isEmpty) fn(from) else onFail(from, errors)
  }
}