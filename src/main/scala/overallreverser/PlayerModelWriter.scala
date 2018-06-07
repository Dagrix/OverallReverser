package overallreverser

import overallreverser.learning.{ClassifierBuilder, TrainingDataBuilder}
import overallreverser.players._
import overallreverser.tools.PlayerIo
import weka.classifiers.Classifier
import weka.core.Instances

/**
  * Created by dagrix on 26/01/2017.
  */
class PlayerModelWriter(classifier: => Classifier, prefixFiles: String) extends App {

  def modelFolder = "./data/models/"
  // iter over all on-disk players
  def playerFolder = "./data/players"
  val allPlayers = PlayerIo.readPlayersInFolder(playerFolder)
  val positions = Seq(PointGuard, ShootingGuard, SmallForward, PowerForward, Center)
  for (pos <- positions) {
    val players = allPlayers.filter(_.position == pos)
    // training data
    val training = new TrainingDataBuilder(players)
    training.writeArff("./data/trainingData" + pos + ".arff")
    // train the classifier
    val clsBuilder = buildClassifier(training.instances, classifier, pos)
    // write on disk
    clsBuilder.writeModel(modelFolder + prefixFiles + pos + ".model")
  }

  def buildClassifier(instances: Instances, classifier: => Classifier, position: Position): ClassifierBuilder = {
    val res = new ClassifierBuilder(instances, classifier)
    res.trainClassifier()
    res
  }
}

