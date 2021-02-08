package one.xingyi.dragons
import one.xingyi.dragons.Combat.{dragonDamaged, dragonKilled}

import java.text.MessageFormat

object Dragon {
  val dead = Dragon(0, alive = false)
}

case class Dragon(hitPoints: Int = 1000, alive: Boolean = true) {
  def damage(damage: Int): AttackResult = hitPoints - damage match {
    case hp if hp <= 0 => AttackResult(Dragon.dead, dragonKilled)
    case hp => AttackResult(copy(hitPoints = hp), dragonDamaged)
  }
}

case class Attack(dragon: Dragon, damage: Int)
object Attack {
  val validateDragonAlive: Validation[Attack] = Validation[Attack](_.dragon.alive, Combat.dragonAlreadyDead)
  val validatePostiveDamage: Validation[Attack] = Validation[Attack](_.damage >= 0, Combat.attackCannotHaveNegativeDamage)
  implicit val validationForAttack: Validation[Attack] = Validation.compose(validateDragonAlive, validatePostiveDamage)
}

case class AttackResult(dragon: Dragon, result: String)
object AttackResult {
  implicit def metricsName: MetricsName[AttackResult] = a => a.result

  implicit val loggerMessage: LoggerMessage[Attack, AttackResult] =
    (at, res) => MessageFormat.format(LoggerMessage.pattern(res.result), at.damage, res.dragon.hitPoints, res.dragon)

  implicit val failedValidation: FailedValidation[Attack, AttackResult] =
    (from: Attack, list: List[String]) => AttackResult(from.dragon, list.head)
}

object Combat {
  val dragonAlreadyDead = "dragon.error.alreadyDeadWhenAttacked"
  val attackCannotHaveNegativeDamage = "dragon.error.cannotHaveNegativeDamage"
  val dragonDamaged = "dragon.damaged"
  val dragonKilled = "dragon.killed"
}

class AttackService(implicit attackNonFunctionals: NonFunctionals[Attack, AttackResult]) {
  val attack = attackNonFunctionals(attack => attack.dragon.damage(attack.damage))
}


object DragonApp extends App {
  implicit val putMetrics = new MapPutMetrics
  implicit val logger = PrintlnLogger

  val combatService = new AttackService

  combatService.attack(Attack(Dragon(), 100))

  println(putMetrics.map)


}
