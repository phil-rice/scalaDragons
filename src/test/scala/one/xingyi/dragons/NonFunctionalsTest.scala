package one.xingyi.dragons
import org.scalatest.matchers.should

trait FunctionFixture extends should.Matchers {
  def fn[From, To](expected: From, to: To): From => To = {
    from =>
      from shouldBe expected
      to
  }
  def fnE[From, To](expected: From, exception: Exception): From => To = {
    from =>
      from shouldBe expected
      throw exception
  }
  def fn2[From1, From2, To](e1: From1, e2: From2, to: To): (From1, From2) => To = {
    (f1, f2) =>
      f1 shouldBe e1
      f2 shouldBe e2
      to
  }
}

class NonFunctionalsTest extends org.scalatest.flatspec.AnyFlatSpec with FunctionFixture {

  behavior of "errorHandler"

  it should "wrap a function and handle any errors" in {
    val e = new RuntimeException
    NonFunctionals.addError(fn2("from", e, "dontcallme"))(fn("from", "to"))("from") shouldBe "to"
    NonFunctionals.addError(fn2("from", e, "to"))(fnE("from", e)("from")) shouldBe "to"
  }

}
