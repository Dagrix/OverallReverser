package overallreverser

import overallreverser.learning.ClassifierBuilder
import weka.classifiers.Classifier
import weka.classifiers.functions.LinearRegression
import weka.core.Instances

/**
  * Created by dagrix on 26/01/2017.
  */
object NeuralNetworkModelWriter
  extends PlayerModelWriter(new weka.classifiers.functions.MultilayerPerceptron(), "neural")


