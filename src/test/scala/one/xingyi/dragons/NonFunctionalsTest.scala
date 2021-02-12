package one.xingyi.dragons
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, times, verify, when}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatestplus.mockito.MockitoSugar

class NonFunctionalsTest extends AnyFlatSpec with should.Matchers with FunctionFixture with MockitoSugar {

  val raw = fn("input", "output")

  behavior of "doWithSideeffect"

  it should "do the original function with some sideeffect" in {
    val sideeffect = mock[(String, String) => Unit]
    NonFunctionals.doWithSideeffect(sideeffect)(raw)("input") shouldBe "output"
    verify(sideeffect, times(1)).apply("input", "output")
  }

  behavior of "logging"

  it should "do the original function and write to the logger with the logging message" in {
    implicit val logger = mock[Logger]
    implicit val loggerMessage: LoggerMessage[String, String] = (from, to) => s"from $from to $to"
    NonFunctionals.logging.apply(raw)("input") shouldBe "output"
    verify(logger, times(1)).message("from input to output")
  }

  behavior of "metrics"

  it should "do the original function and send the output to the metrics" in {
    implicit val putMetrics = mock[PutMetrics]
    implicit val metricsName: MetricsName[String] = (s) => s"${s}_name"
    NonFunctionals.metrics[String, String].apply(raw)("input") shouldBe "output"
    verify(putMetrics, times(1)).addOne("output")
  }

  behavior of "MapPutMetrics"

  it should "count how many times it is called" in {
    implicit val metricsName: MetricsName[String] = (s) => s"${s}_name"
    val pm = new MapPutMetrics
    pm.addOne("one")
    pm.addOne("one")
    pm.addOne("one")
    pm.addOne("two")
    pm.addOne("two")
    pm.map.map { case (k, v) => (k, v.get) } shouldBe Map("one_name" -> 3, "two_name" -> 2)

  }

  behavior of "error"

  it should "do the original function if there was no problem " in {
    implicit val errorStrategy = mock[ErrorStrategy[String, String]]
    NonFunctionals.error[String, String].apply(raw)("input") shouldBe "output"
    verify(errorStrategy, times(0)).apply(any[String], any[Exception])
  }
  it should "do the original function then handle the error " in {
    implicit val errorStrategy = mock[ErrorStrategy[String, String]]
    val runtimeException = new RuntimeException("some message")
    when(errorStrategy.apply("input", runtimeException)).thenReturn("outputFromErrorStrategy")
    NonFunctionals.error[String, String].apply(_ => throw runtimeException)("input") shouldBe "outputFromErrorStrategy"
  }

  behavior of "default error strategy"

  it should "just rethrow the error" in {
    val errorStrategy = implicitly[ErrorStrategy[String, String]]
    val runtimeException = new RuntimeException("some message")
    the[RuntimeException].thrownBy(errorStrategy("input", runtimeException)) shouldBe runtimeException
  }

  behavior of "Validation"

  it should "return the result of the raw function if there are no validation issues" in {
    implicit val validation: Validation[String] = s => fn("input", List[String]())(s)
    implicit val validationFailure: FailedValidation[String, String] = (s, errors) => fail("should not be called")
    NonFunctionals.validate[String, String].apply(raw)("input") shouldBe "output"
  }

  it should "return the result of the validation failure if there are validation issues" in {
    implicit val validation: Validation[String] = s => fn("input", List("reasonOne", "reasonTwo"))(s)
    implicit val validationFailure: FailedValidation[String, String] = (s, errors) => fn(("input", List("reasonOne", "reasonTwo")), "outputFromFailure")(s, errors)
    NonFunctionals.validate[String, String].apply(raw)("input") shouldBe "outputFromFailure"
  }

  behavior of "compose"

  type DelegateFn = (String => String) => (String => String)
  it should "call the functions one after another" in {
    val fn1: DelegateFn = raw => from => raw(from) + "_1"
    val fn2: DelegateFn = raw => from => raw(from) + "_2"
    val fn3: DelegateFn = raw => from => raw(from) + "_3"
    NonFunctionals.compose(fn1, fn2, fn3)(raw)("input") shouldBe "output_1_2_3"
  }

}
