package one.xingyi.dragons
import org.scalatest.matchers.should.Matchers

import java.util.concurrent.atomic.AtomicReference

trait FunctionFixture extends Matchers {
  def fn[X, Y](expected: X, y: => Y): X => Y = { x: X => x shouldBe expected; y }

}