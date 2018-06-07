package overallreverser

import java.io.File
import java.net.URI

import com.sun.deploy.net.URLEncoder
import overallreverser.learning.{ClassifierBuilder, TrainingDataBuilder}
import overallreverser.parsing.TwoKMTCentralPlayerParser
import overallreverser.players._
import overallreverser.tools.{Io, PlayerIo}

import scala.io.Source

/**
  * Created by dagrix on 25/01/2017.
  */
object CrossValidation extends App {

  // iter over all on-disk players
  val folder = "./data/players"
  val players = new File(folder).listFiles().map { file =>
    val path = file.getPath
    val source = Source.fromFile(path)
    val lines = source.getLines().toVector
    source.close()
    PlayerIo.playerFromeasyLineBasedFormat(lines)
  }//.filter(_.position == PowerForward)
  val training = new TrainingDataBuilder(players)
  training.writeArff("./data/trainingData.arff")

  // crossvalidate
  val clsBuilder = new ClassifierBuilder(training.instances)
  //clsBuilder.playerByPlayerReport(players)
  clsBuilder.crossValidationReport()
}
