package overallreverser.learning

import overallreverser.players.{Attributes, Player}
import weka.core.Attribute

/**
  * Created by dagrix on 26/01/2017.
  */
object FeatureExtraction {

  /** For each feature, provide its name, and how to extract it from one player. */
  val featureExtractors: Seq[(String, Player => Double)] = {
    // player details
    Seq(
      ("height", (p: Player) => p.height.toDouble),
      ("weight", (p: Player) => p.weight.toDouble)
    ) ++
    // attributes
    Attributes.ATTRIBUTE_NAMES.map { attributeName =>
      (attributeName, (p: Player) => p.attributes(attributeName).toDouble)
    } :+
    // overall as the class value
      (("overall", (p: Player) => p.overall.toDouble))
  }

  def features(player: Player): Array[Double] = {
    val res = Array.ofDim[Double](wekaAttributes.size())
    for (((_, extract), i) <- FeatureExtraction.featureExtractors.zipWithIndex) {
      res(i) = extract(player)
    }
    res
  }

  lazy val wekaAttributes: java.util.ArrayList[Attribute] = {
    val atts = new java.util.ArrayList[Attribute]()
    FeatureExtraction.featureExtractors.foreach { case (attrName, _) =>
      atts.add(new Attribute(attrName))
    }
    atts
  }

  def maxFeatureNameLength: Int = featureExtractors.maxBy { case (name, _) =>
    name.length
  }._1.length
}
