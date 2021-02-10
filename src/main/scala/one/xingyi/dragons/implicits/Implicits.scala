package one.xingyi.dragons.implicits

object ImplicitsExample1 {
  //Here we define an implicit
  //We mean by this 'if any method in scope needs an implicit int, here it is'
  //This is just 'dependencyInjection' like autowiring
  implicit def someNameThatDoesntMatter: Int = 4


  //Here we use the dependency injection. We say 'I need a dependency injected value of type Int
  //We only allow a single one to be in scope (It's a compiler error otherwise)
  def aMethodThatNeedsAnImplicit(a: Int)(implicit b: Int) = a + b

  println(aMethodThatNeedsAnImplicit(1))

  println(implicitly[Int])
}


object ImplicitsExample2 {
  //Here we define an implicit
  //We mean by this 'if any method in scope needs an implicit String, here it is'
  //This is just 'dependencyInjection' like autowiring
  implicit def someNameThatDoesntMatter: String = "4"


  //Here we use the dependency injection. We say 'I need a dependency injected value of type String
  //We only allow a single one to be in scope (It's a compiler error otherwise)
  def aMethodThatNeedsAnImplicit(a: String)(implicit b: String) = a + b

  println(aMethodThatNeedsAnImplicit("someString"))
  println(implicitly[String])

}

object ImplicitsExample3 {
  implicit def someNameThatDoesntMatter: String = "4"
  implicit def someNameThatAlsoDoesntMatter: String = "4"
  //  println(implicitly[String])  // doesn't compile because there are two implicits in scope

}


class MyThing[T](implicit logMessageFn: T => String ){
  def doSomething(t: T) = println(logMessageFn(t))
}

object MyThing{

  def use[T](t: T)(implicit logMessageFn: T => String): Unit ={
    println(logMessageFn(t))
  }

  implicit def defaultMessageFn[T]: T => String = t => t.toString  //we can define defaults
  implicit val MessageFunctionForString: String => String = t => t  // we can be more precise if we want to be
  implicit val MessageFunctionForInt: Int => String = t => t.toString

  use(1)
  use("one")
  use(1.0)

}