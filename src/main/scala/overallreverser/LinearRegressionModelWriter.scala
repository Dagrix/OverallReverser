package overallreverser

import java.io.{BufferedWriter, FileWriter}

import overallreverser.learning.{ClassifierBuilder, FeatureExtraction}
import overallreverser.players.Position
import weka.classifiers.Classifier
import weka.classifiers.functions.LinearRegression
import weka.core.Instances

/**
  * Created by dagrix on 26/01/2017.
  */
object LinearRegressionModelWriter
  extends PlayerModelWriter(
    { val res = new weka.classifiers.functions.LinearRegression(); res.setRidge(1.0); res},
    "linear") {

  override def buildClassifier(instances: Instances, classifier: => Classifier, pos: Position): ClassifierBuilder = {
    val cls = classifier.asInstanceOf[LinearRegression]
    val res = new ClassifierBuilder(instances, cls)
    res.trainClassifier()
    // print the attribute with their coefficients
    println(cls.coefficients().mkString(" "))
    writeCoefficients(modelFolder + "coeffs" + pos + ".txt", cls.coefficients())
    res
  }

  def attributeWithWeights(coeffs: Array[Double]): Seq[(String, Double)] = {
    FeatureExtraction.featureExtractors.zip(coeffs).map { case ((attrName, _), coeff) =>
      (attrName, coeff)
    }
  }

  def writeCoefficients(path: String, coeffs: Array[Double]): Unit = {
    val bw = new BufferedWriter(new FileWriter(path))
    val maxLength = FeatureExtraction.maxFeatureNameLength + 2
    // print each attribute with coeff, as they're presented on 2KMTcentral
    for (((attrName, _), i) <- FeatureExtraction.featureExtractors.zipWithIndex) {
      bw.write(attrName.padTo(maxLength, ' ') + coeffs(i).toString)
      bw.newLine()
    }
    // print the constant
    bw.write("CONSTANT".padTo(maxLength, ' ') + coeffs.last)
    bw.newLine()
    // print the top 10 most important attributes
    bw.write("============= Top 20 most important ========================\n")
    topNAttributes(20, attributeWithWeights(coeffs)).zipWithIndex.foreach { case ((name, coeff), i) =>
      bw.write((i + 1).toString + ". " + name + displayedFraction(coeff, coeffs))
      bw.newLine()
    }
    bw.write("============= Useless ========================\n")
    attributeWithWeights(coeffs).filter(_._2 == 0.0).foreach { case (name, coeff) =>
      bw.write(name)
      bw.write(", ")
    }
    bw.close()
  }

  def topNAttributes(n: Int, weightedAttributes: Seq[(String, Double)]): Seq[(String, Double)] = {
    weightedAttributes.sortBy(_._2).reverse.take(n)
  }

  def fractionOverall(coeff: Double, coeffs: Array[Double]): Double = {
    // we don't take the constant into consideration
    val sum = coeffs.sum - coeffs.last
    coeff / sum
  }

  def displayedFraction(coeff: Double, coeffs: Array[Double]): String = {
    val frac = fractionOverall(coeff, coeffs)
    val int = (frac * 1000).toInt
    val percent = int.toDouble / 10.0
    // print only if > 10%
    if (percent >= 10.0)
      " (" + percent + "%)"
    else ""
  }
}
