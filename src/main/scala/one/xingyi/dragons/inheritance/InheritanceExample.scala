package one.xingyi.dragons.inheritance

trait ToJson {
  def toJson: String
}

//We can now use ToJson by modifying classes to use it
//This typically puts non domain logic into domain classes
case class Thing(name: String) extends ToJson {
  def toJson = s""""name":"$name"""""
}

object ToJsonApp {

  val s: String = "someString"
  val i: Int = 3

  //so try and add toJson to these strings and ints

}

