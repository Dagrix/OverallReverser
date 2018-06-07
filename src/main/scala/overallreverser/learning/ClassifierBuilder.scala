package overallreverser.learning

import java.util.Random

import overallreverser.players.Player
import weka.classifiers.evaluation.output.prediction.PlainText
import weka.core.{DenseInstance, Instance, Instances}
import weka.classifiers.{Classifier, Evaluation}
import weka.classifiers.functions.LinearRegression
import weka.filters.Filter

import scala.collection.JavaConverters._

/**
  * Created by dagrix on 26/01/2017.
  */
class ClassifierBuilder(val instances: Instances,
                        val classifier: Classifier = { val res = new LinearRegression(); res.setRidge(1.0); res }) {

  def this(instances: Instances, path: String) = {
    this(instances, weka.core.SerializationHelper.read(path).asInstanceOf[Classifier])
  }

  def trainClassifier(): Unit = { classifier.buildClassifier(instances) }

  def predictOverall(player: Player): Double = {
    classifier.classifyInstance(instanceOf(player))
  }

  def instanceOf(player: Player): Instance = {
    val res = new DenseInstance(1.0, FeatureExtraction.features(player))
    res.setDataset(instances)
    res
  }

  def crossValidationReport(): Unit = {
    trainClassifier()
    // evaluate
    val evaluation = new Evaluation(instances)
    evaluation.crossValidateModel(classifier, instances, 10, new Random(0))
    println(evaluation.toSummaryString("\nResults\n======\n", false))
  }

  def playerByPlayerReport(players: Seq[Player]): Unit = {
    val (train, test) = trainTestSplit(0.7)
    trainClassifier()
    // test
    for (i <- 0 until test.numInstances()) {
      val pred = classifier.classifyInstance(test.instance(i))
      val playerIndex = train.size() + i
      println("============")
      println(players(playerIndex))
      println("Actual overall: " + players(playerIndex).overall)
      println("Predicted: " + pred)
    }
  }

  def trainTestSplit(fractionTrain: Double): (Instances, Instances) = {
    val trainSize = Math.round(instances.numInstances() * fractionTrain).toInt
    val testSize = instances.numInstances() - trainSize
    val train = new Instances(instances, 0, trainSize)
    val test = new Instances(instances, trainSize, testSize)
    (train, test)
  }

  def writeModel(path: String): Unit = {
    weka.core.SerializationHelper.write(path, classifier)
  }
}
