package one.xingyi.dragons

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class DragonSpec extends AnyFlatSpec with should.Matchers {


  behavior of "Dragon"

  it should "be created with a 1000 hitpoints and be alive" in {
    Dragon() shouldBe Dragon(hitPoints = 1000, alive = true)
  }

  behavior of "Dragon.damage"


  it should "create a new dragon with the hitpoints reduced by the damage" in {
    def attack(d: Dragon, damage: Int): Dragon = {
      val AttackResult(dragon, result) = d.damage(damage)
      result shouldBe Combat.dragonDamaged
      dragon
    }
    attack(Dragon(), 100) shouldBe Dragon(900)
    attack(Dragon(900), 100) shouldBe Dragon(800)
    attack(Dragon(900), 100) shouldBe Dragon(800)
    attack(Dragon(1000), 999) shouldBe Dragon(1)
  }

  it should "kill the dragon and have zero hitpoints if the hitpoints are reduced to zero or fewer" in {
    def kill(d: Dragon, damage: Int): Dragon = {
      val AttackResult(dragon, result) = d.damage(damage)
      result shouldBe Combat.dragonKilled
      dragon
    }
    kill(Dragon(), 1000) shouldBe Dragon.dead
    kill(Dragon(), 1001) shouldBe Dragon.dead
    kill(Dragon(1000), 10000) shouldBe Dragon.dead
  }

  behavior of "Attack validation"
  def validate(d: Dragon, damage: Int) = implicitly[Validation[Attack]].apply(Attack(d, damage))

  it should "validate the dragon needs to be alive" in {
    validate(Dragon(), 100) shouldBe List()
    validate(Dragon.dead, 100) shouldBe List(Combat.dragonAlreadyDead)
  }
  it should "validate the damage is positive" in {
    validate(Dragon(100), -1) shouldBe List(Combat.attackCannotHaveNegativeDamage)
    validate(Dragon(1000), -100) shouldBe List(Combat.attackCannotHaveNegativeDamage)
  }

  behavior of "Attack Logging message"

  it should "have a nice message for all cases" in {
    val message = implicitly[LoggerMessage[Attack, AttackResult]]
    message(Attack(Dragon(), 100), AttackResult(Dragon(), Combat.dragonDamaged)) shouldBe "The dragon was hit for 100 damage, It has 1,000 hitpoints left"
    message(Attack(Dragon(), 100), AttackResult(Dragon(), Combat.dragonKilled)) shouldBe "The dragon was hit for 100 damage and is now dead!"
    message(Attack(Dragon(), -100), AttackResult(Dragon(), Combat.attackCannotHaveNegativeDamage)) shouldBe "It is not possible to attack for -ve damage. The amount was -100"
    message(Attack(Dragon(), 100), AttackResult(Dragon(), Combat.dragonAlreadyDead)) shouldBe "The dragon was already dead when attacked for 100"

  }

  behavior of "Attack Metrics Name"
  it should "have a metrics name" in {
    val metricsName = implicitly[MetricsName[AttackResult]]
    metricsName(AttackResult(Dragon(), "someName")) shouldBe "someName"
  }


}
