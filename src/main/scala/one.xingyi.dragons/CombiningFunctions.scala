package one.xingyi.dragons
import java.util

object CombiningFunctions {

  //let's look at functions that return functions


  def lift[From, To](f: From => To): List[From] => List[To] = from => from.map(f)

  def add(a: Int)(b: Int) = a + b

  def addOne: Int => Int = add(1)
  def addTwo: Int => Int = add(2)


  def sqrt(i: Int) = Math.sqrt(i).toInt

  val addOneToList: List[Int] => List[Int] = lift(addOne)


  def liftToString(f: Int => Int): String => String =
    (from: String) => f(from.toInt).toString

  liftToString(addOne)
  liftToString(addTwo)
  liftToString(sqrt)


  addOneToList(List(1, 2, 3))


  def pipeline[A, B, C](fn1: A => B, fn2: B => C): A => C = a => fn2(fn1(a))
  def pipelineWithValidation[A, B, C](fn1: A => B, fn2: B => C): A => C = a => fn2(fn1(a))


  def map[T, T1](list: List[T], fn: T => T1) = {
    val result = new util.ArrayList[T1]()
    for (i <- 0 to list.size - 1) {
      result.add(fn(list(i)))
    }
    result
  }


}

trait Monoid [T]{
  def zero: T
  def plus(t1: T, t2: T): T

}
object Monoid {

  implicit val monoidForInt: Monoid[Int] = new Monoid[Int] {
    override def zero: Int = 0
    override def plus(t1: Int, t2: Int): Int = t1 + t2
  }

  implicit val monoidForString: Monoid[String] = new Monoid[String] {
    override def zero: String = ""
    override def plus(t1: String, t2: String): String = t1 + t2
  }

  implicit val monoidForDouble: Monoid[Double] = new Monoid[Double] {
    override def zero: Double = 0
    override def plus(t1: Double, t2: Double): Double = t1 + t2
  }
}

