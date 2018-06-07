package overallreverser.tools

import java.io.{BufferedWriter, File, FileWriter}

import overallreverser.players._

import scala.io.Source

/**
  * Handles IO on players, mostly conversions in and out of various readable formats.<
  * Created by dagrix on 25/01/2017.
  */
object PlayerIo {

  def queryByName(name: String, players: Seq[Player]): Option[Player] = {
    val matches = players.filter(_.name == name)
    if (matches.nonEmpty)
      Some(matches.sortBy(p => - (p.overall * 100 - p.attributes("Intangibles"))).head) /* send best player with that name */
    else None
  }

  def writePlayer(player: Player, folder: String): Unit = {
    val filename = fileName(player)
    val path = folder + "/" + filename + ".txt"
    val bw = new BufferedWriter(new FileWriter(path))
    bw.write(easyLineBasedFormat(player))
    bw.close()
  }

  def readPlayersInFolder(folder: String): Seq[Player] = {
    new File(folder).listFiles().map { file =>
      val path = file.getPath
      val source = Source.fromFile(path)
      val lines = source.getLines().toVector
      source.close()
      playerFromeasyLineBasedFormat(lines)
    }
  }

  def easyLineBasedFormat(player: Player): String = {
    val sb = new StringBuilder()
    sb ++= player.id + "\n"
    sb ++= player.name + "\n"
    sb ++= player.position + "\n"
    sb ++= player.overall + "\n"
    sb ++= player.height + "\n"
    sb ++= player.weight + "\n"
    for (attributeName <- Attributes.ATTRIBUTE_NAMES) {
      sb ++= player.attributes(attributeName) + "\n"
    }
    sb.toString()
  }

  def playerFromeasyLineBasedFormat(lines: Seq[String]): Player = {
    val iter = lines.toIterator
    val id = iter.next()
    val name = iter.next()
    val position = Position.parse(iter.next())
    val overall = iter.next().toInt
    val height = iter.next().toInt
    val weight = iter.next().toInt
    val res = new PlayerBuilder(
      id = id,
      name = name,
      position = position,
      overall = overall,
      height = height,
      weight = weight
    )
    for (attributeName <- Attributes.ATTRIBUTE_NAMES) {
      res.attributes(attributeName) = iter.next().toInt
    }
    res
  }

  // TODO
  def json(player: Player): String = {
    ""
  }

  def fileName(player: Player): String = {
    player.overall + "_" + pathSafeName(player.name) + player.id
  }

  val SAFE_NAME_CHARACTER = "[0-9a-zA-Z-.,_]"
  def pathSafeName(name: String): String = {
    name.map {
      case c if c.toString.matches(SAFE_NAME_CHARACTER) => c
      case _ => '_'
    }
  }
}
