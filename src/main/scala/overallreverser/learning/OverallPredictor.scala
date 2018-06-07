package overallreverser.learning

import overallreverser.players._

/**
  * Created by dagrix on 27/01/2017.
  */
class OverallPredictor(val players: Seq[Player]) {

  val positions = Vector(PointGuard, ShootingGuard, SmallForward, PowerForward, Center)
  val playersByPosition = positions.map { position =>
    players.filter(_.position == position)
  }
  val data = playersByPosition.map { players =>
    new TrainingDataBuilder(players)
  }
  val classifiers = data.map { tdb =>
    val cls = new ClassifierBuilder(tdb.instances)
    cls.trainClassifier()
    cls
  }

  def predictOverall(player: Player): Double = {
    predictOverall(player, player.position)
  }

  def predictOverall(player: Player, position: Position): Double = {
    classifiers(positions.indexOf(position)).predictOverall(player)
  }
}
