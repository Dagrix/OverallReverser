package overallreverser.players

import scala.collection.mutable

/**
  * Created by dagrix on 25/01/2017.
  */
class PlayerBuilder(val id: String = "NOBODY",
                    val name: String = "Nobody",
                    val position: Position = PointGuard,
                    val overall: Int = 25,
                    val height: Int = 6*12 /* in inches */ ,
                    val weight: Int = 180 /* in lbs */) extends Player {

  val attributes: mutable.Map[String, Int] = buildAttributes(lowestValues)

  def buildAttributes(values: Iterator[Int]): mutable.Map[String, Int] = {
    val res = mutable.Map[String, Int]()
    Attributes.ATTRIBUTE_NAMES.foreach { attributeName =>
      res(attributeName) = values.next()
    }
    res
  }

  private[this] def lowestValues: Iterator[Int] = new Iterator[Int] {
    override def hasNext: Boolean = true
    override def next(): Int = 25
  }
}