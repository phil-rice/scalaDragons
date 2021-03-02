package one.xingyi.dragons


import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.{Level, Logger}


object Dragon1 extends App {
  private[dragons] val logger = Logger.getLogger("Dragon1")
  private[dragons] val damageCount = new AtomicInteger
  def counts: Int = damageCount.get
  var freshDragon = new Dragon1(1000, true)

  System.out.println("Killing Dragons for Fun and Profit")
  val d1 = Dragon1.freshDragon
  val d2 = d1.damage(100)
  val d3 = d2.damage(100)
  val d4 = d2.damage(900)
  for (d <- List(d1, d2, d3, d4)) {println(d) }
  println("Your dragon is " + (if (d4.alive) "alive" else "dead"))
  println()
  println("Metrics are: ")
  println(Dragon1.damageCount.get)
}
case class Dragon1(hitpoints: Int = 1000, alive: Boolean = true) {
  private[dragons] def isDead = !alive

  def damage(damage: Int): Dragon1 = try {
    if (damage <= 0 || isDead) return this
    val newHitpoints = hitpoints - damage
    if (newHitpoints <= 0) {
      Dragon1.logger.info("dragon was hit for " + damage + " and is now DEAD!")
      new Dragon1(0, false)
    }
    else {
      Dragon1.damageCount.incrementAndGet
      Dragon1.logger.info("damage dragon for " + damage + "hitpoints. Hitpoints now" + newHitpoints)
      new Dragon1(newHitpoints, alive)
    }
  } catch {
    case e: RuntimeException =>
      Dragon1.logger.log(Level.SEVERE, "Unexpected error damaging " + this + " for " + damage + " hitpoints", e)
      throw e
  }
}
