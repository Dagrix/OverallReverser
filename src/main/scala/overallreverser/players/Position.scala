package overallreverser.players

/**
  * Created by dagrix on 25/01/2017.
  */
sealed abstract class Position

case object PointGuard extends Position
case object ShootingGuard extends Position
case object SmallForward extends Position
case object PowerForward extends Position
case object Center extends Position

object Position {
  def parse(pos: String): Position = {
    if (pos == PointGuard.toString) PointGuard
    else if (pos == ShootingGuard.toString) ShootingGuard
    else if (pos == SmallForward.toString) SmallForward
    else if (pos == PowerForward.toString) PowerForward
    else if (pos == Center.toString) Center
    else throw new Exception("You should only use this method on guaranteed Position case classes String representations.")
  }
}