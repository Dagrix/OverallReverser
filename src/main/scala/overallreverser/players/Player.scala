package overallreverser.players

/**
  * Created by dagrix on 25/01/2017.
  */
trait Player {

  def id: String

  def name: String

  def position: Position

  def height: Int /* in inches */

  def weight: Int /* in lbs */

  def attributes: collection.Map[String, Int]

  def overall: Int /* the overall as marked on the card, also determines color (rarity) */

  override def toString: String = {
    val sb = new StringBuilder()
    sb ++= name
    sb ++= " ("
    sb ++= position.toString
    sb ++= ", " + height + "in"
    sb ++= ", " + weight + "lbs"
    sb ++= ", " + overall + " ovr)"
    sb ++= "\n"
    sb ++= attributes.toString()
    sb.toString()
  }
}
