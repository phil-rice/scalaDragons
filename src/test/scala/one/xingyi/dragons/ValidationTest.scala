package one.xingyi.dragons
import org.scalatest.matchers.should

class ValidationTest extends org.scalatest.flatspec.AnyFlatSpec with should.Matchers {

  behavior of "Validation.compose"

  val v1 = Validation[String](s => s.startsWith("a"), "should start with a")
  val v2 = Validation[String](s => s.endsWith("b"), "should end with b")
  val composed = Validation.compose(v1, v2)
  it should "created a validator from a condition function and a string" in {
    v1("a12") shouldBe List()
    v1("b12") shouldBe List("should start with a")
    v2("12b") shouldBe List()
    v2("b12") shouldBe List("should end with b")
  }

  it should "compose multiple validators" in {
    composed("a12b") shouldBe List()
    composed("a12") shouldBe List("should end with b")
    composed("b12") shouldBe List("should start with a", "should end with b")
    composed("12b") shouldBe List("should start with a")
    composed("b12b") shouldBe List("should start with a")

  }


}
