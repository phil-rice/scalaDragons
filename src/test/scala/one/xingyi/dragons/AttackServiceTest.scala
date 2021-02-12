package one.xingyi.dragons
import one.xingyi.dragons.Attack.{validateDragonAlive, validatePostiveDamage}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should


class AttackServiceTest extends AnyFlatSpec with should.Matchers with FunctionFixture {

  behavior of "AttackResult.attack"

  val attack = Attack(Dragon(123, false), 345)
  val rawAttackResult = AttackResult(Dragon(345, true), "someResult")
  val liftedAttackResult = AttackResult(Dragon(345, true), "someLiftedResult")

  it should "call modify the rawAttack with the non functionas" in {
    val rawAttack: Attack => AttackResult = fn(attack, rawAttackResult)
    implicit val liftedAttack: NonFunctionals[Attack, AttackResult] = raw => fn(rawAttack, fn(attack, liftedAttackResult))(raw)

    val service = new AttackService(rawAttack)

    service.attack(attack) shouldBe liftedAttackResult

  }

  behavior of "validateDragonAlive"

  it should "validation whether the dragon is alive" in {
    validateDragonAlive(Attack(Dragon(), 100)) shouldBe List()
    validateDragonAlive(Attack(Dragon(), -100)) shouldBe List()
    validateDragonAlive(Attack(Dragon(alive = false), 100)) shouldBe List(Combat.dragonAlreadyDead)
    validateDragonAlive(Attack(Dragon(alive = false), -100)) shouldBe List(Combat.dragonAlreadyDead)
  }
  it should "validation whether the damage is positive" in {
    validatePostiveDamage(Attack(Dragon(), 100)) shouldBe List()
    validatePostiveDamage(Attack(Dragon(alive = false), 100)) shouldBe List()
    validatePostiveDamage(Attack(Dragon(), -100)) shouldBe List(Combat.attackCannotHaveNegativeDamage)
    validatePostiveDamage(Attack(Dragon(alive = false), -100)) shouldBe List(Combat.attackCannotHaveNegativeDamage)
  }
}
