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

  def validate(d: Dragon, damage: Int) = {
    val AttackResult(dragon, result) = d.damage(damage)
    result shouldBe Combat.dragonError
    dragon
  }
  it should "validate the dragon is alive" in {
    validate(Dragon.dead, 100) shouldBe Dragon.dead
  }
  it should "validate the damage is positive" in {
    validate(Dragon(100), -1) shouldBe Dragon(100)
    validate(Dragon(1000), -100) shouldBe Dragon(1000)
  }

}