package overallreverser.learning

import overallreverser.players.Player
import weka.core.converters.ArffSaver
import weka.core.{Attribute, DenseInstance, Instances}
import java.io.File

import weka.filters.Filter
import weka.filters.unsupervised.attribute.{Normalize, Standardize}

/**
  * Created by dagrix on 25/01/2017.
  */
class TrainingDataBuilder(val players: Seq[Player]) {

  lazy val attributes: java.util.ArrayList[Attribute] = FeatureExtraction.wekaAttributes

  lazy val instances: Instances = {
    val data = new Instances("NBA2K Players", attributes, players.size + 1)
    for (player <- players) {
      data.add(new DenseInstance(1.0, FeatureExtraction.features(player)))
    }
    if (data.classIndex() == -1)
      data.setClassIndex(data.numAttributes() - 1)
    // apply filters
    /*
    val filter = new Standardize()
    filter.setInputFormat(data)
    Filter.useFilter(data, filter)
    */
    // we won't use a filter for now, linear regression doesn't need it
    data
  }

  def writeArff(path: String): Unit = {
    val saver = new ArffSaver()
    saver.setInstances(instances)
    saver.setFile(new File(path))
    saver.writeBatch()
  }
}
