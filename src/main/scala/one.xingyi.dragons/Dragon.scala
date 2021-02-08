package one.xingyi.dragons
import one.xingyi.dragons.Combat.{dragonDamaged, dragonError, dragonKilled}

object Dragon {
  val dead = Dragon(0, alive = false)
}

case class Dragon(hitPoints: Int = 1000, alive: Boolean = true) {
  def damage(damage: Int): AttackResult = {
    val validAttack = damage >= 0 && alive
    (validAttack, hitPoints - damage) match {
      case (false, _) => AttackResult(this, dragonError)
      case (true, hp) if hp <= 0 => AttackResult(Dragon.dead, dragonKilled)
      case (true, hp) => AttackResult(copy(hitPoints = hp), dragonDamaged)
    }
  }
}

case class Attack(dragon: Dragon, damage: Int)
case class AttackResult(dragon: Dragon, result: String)

object AttackResult {
  implicit def metricsName: MetricsName[AttackResult] = a => a.result
  implicit val loggerMessage: LoggerMessage[Attack, AttackResult] =
    (at, res) => s"Attacked the dragon for ${at.damage}. Result was ${res.result} and the new dragon is ${res.dragon}"
}

object Combat {
  val dragonError = "dragon.error"
  val dragonDamaged = "dragon.damaged"
  val dragonKilled = "dragon.killed"
}

class CombatService(implicit attackNonFunctionals: NonFunctionals[Attack, AttackResult]) {
  val attack = attackNonFunctionals(attack => attack.dragon.damage(attack.damage))
}


object DragonApp extends App {
  implicit val putMetrics = new MapPutMetrics
  implicit val logger = PrintlnLogger

  val combatService = new CombatService

  combatService.attack(Attack(Dragon(), 100))

  println(putMetrics.map)


}
