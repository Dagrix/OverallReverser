package overallreverser

import overallreverser.learning.{ClassifierBuilder, OverallPredictor, TrainingDataBuilder}
import overallreverser.players._
import overallreverser.tools.PlayerIo

/**
  * Created by dagrix on 26/01/2017.
  */
object Tester extends App {
  val position = PointGuard
  val players = PlayerIo.readPlayersInFolder("./data/players")
  val ovr = new OverallPredictor(players)
  val allPG = players.map { p => (p, ovr.predictOverall(p, position)) }
  val pgRanking = allPG.sortBy(-_._2)
  pgRanking.take(20).zipWithIndex.foreach { case ((p, overall), rank) =>
    println((rank+1) + ". " + p.name + "(" + p.overall + ") => " + overall)
  }
  /*
  val lillard = PlayerIo.queryByName("Damian Lillard", players).get
  println ("Lillard: ")
  println(lillard)
  println("Predicted: " + ovr.predictOverall(lillard))
  val spencer = PlayerIo.queryByName("Spencer Haywood", players).get
  println(spencer)
  println("Predicted: " + ovr.predictOverall(spencer))
  */
}
