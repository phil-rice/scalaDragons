package one.xingyi.dragons


import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.{Level, Logger}


object DragonApp extends App {

  var freshDragon = new Dragon1(1000, true)

  System.out.println("Killing Dragons for Fun and Profit")
  val d1 = freshDragon
  val d2 = d1.damage(100)
  val d3 = d2.damage(100)
  val d4 = d2.damage(900)
  for (d <- List(d1, d2, d3, d4)) {println(d) }
  println("Your dragon is " + (if (d4.alive) "alive" else "dead"))
  println()
  println("Metrics are: ")
  println(Dragon1.damageCount.get)
}


object Dragon1 {
  private[dragons] val logger = Logger.getLogger("Dragon1")
  private[dragons] val damageCount = new AtomicInteger

  def errorHandler: (Attack, Exception) => DragonAndResult = {
    case (attack, e: Exception) =>
      Dragon1.logger.log(Level.SEVERE, "Unexpected error damaging " + this + " for " + attack.damage + " hitpoints", e)
      throw e
  }
  def dragonLogMessageFn(attack: Attack, result: DragonAndResult) =
    result.result match {
      case "dragon.attack.killed" => "dragon was hit for " + attack.damage + " and is now DEAD!"
      case "dragon.attack.damaged" => "damage dragon for " + attack.damage + "hitpoints. Hitpoints now" + result.dragon1.hitpoints
    }
  def putMetricsForDragon: PutMetrics = _ => Dragon1.damageCount.incrementAndGet()
}

case class Attack(dragon1: Dragon1, damage: Int)
case class DragonAndResult(dragon1: Dragon1, result: String)

case class Dragon1(hitpoints: Int = 1000, alive: Boolean = true) {
  private[dragons] def isDead = !alive

  //scaffolding  and what a mess!
  def damage(damage: Int): Dragon1 =
    NonFunctionals.addError[Attack, DragonAndResult](Dragon1.errorHandler)(
      NonFunctionals.addLogging(PrintlnLogPrinter, Dragon1.dragonLogMessageFn)(
        NonFunctionals.addMetrics(Dragon1.putMetricsForDragon, Dragon1.dragonLogMessageFn)(
          attack)))(Attack(this, damage)).dragon1

  def attack(attack: Attack): DragonAndResult = {
    if (attack.damage <= 0 || isDead) return DragonAndResult(this, "dragon.validationError")
    val newHitpoints = hitpoints - attack.damage
    if (newHitpoints <= 0)
      DragonAndResult(Dragon1(0, false), "dragon.attack.killed")
    else
      DragonAndResult(new Dragon1(newHitpoints, alive), "dragon.attack.damaged")

  }


  val attackNonFunctionals = NonFunctionals.compose[Attack, DragonAndResult](
    NonFunctionals.addError[Attack, DragonAndResult](Dragon1.errorHandler),
    NonFunctionals.addLogging(PrintlnLogPrinter, Dragon1.dragonLogMessageFn),
    NonFunctionals.addMetrics(Dragon1.putMetricsForDragon, Dragon1.dragonLogMessageFn))

  def damage2(damage: Int): Dragon1 = attackNonFunctionals(attack)(Attack(this, damage)).dragon1


}
