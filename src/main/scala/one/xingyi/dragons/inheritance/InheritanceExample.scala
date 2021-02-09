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

  //How do I modify String and Int so that they implement toJson
  //How do I implement two versions of toJson for version1 and version2 of the api?


}

