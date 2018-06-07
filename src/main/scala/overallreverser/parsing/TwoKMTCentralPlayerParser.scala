package overallreverser.parsing

import overallreverser.players._
import overallreverser.tools.{Io, PlayerIo}

import scala.util.matching.Regex

/**
  * Created by dagrix on 25/01/2017.
  */
class TwoKMTCentralPlayerParser {

  // the regex is a bit too brutal (.+)
  val ATTRIBUTE_REGEX = ("<li class=\"attribute\"><span class=\"[^\"]*\" data-original-value=\"([0-9]+)\"" +
    " data-dynamic-duo-boost=\"([0-9]*)\">([0-9]+)</span> (.+)</li>").r
  def makeStandardDetailsRegex(detailName: String): Regex =
    s"<th>$detailName</th>\\s*<td>(.+)</td>".r
  val NAME_REGEX = makeStandardDetailsRegex("Name")
  val THEME_REGEX = makeStandardDetailsRegex("Theme")
  val POSITION_REGEX = "<th>Position</th>\\s*<td><a href=\"[^\"]*\">(.+?)</a>.*</td>".r
  val HEIGHT_REGEX = makeStandardDetailsRegex("Height")
  val FEET_INCHES_REGEX = "([0-9]+)'([0-9]+)\" \\([0-9]+cm\\)".r
  val WEIGHT_REGEX = makeStandardDetailsRegex("Weight")
  val LBS_REGEX = "([0-9]+)lbs \\([0-9]+kg\\)".r
  val OVERALL_REGEX = "<h4 class=\"attribute-header\">(.+) <strong>Overall</strong></h4>".r
  val URL_REGEX = "http://2kmtcentral.com/17/players/([0-9]+)/.*".r

  def parsePlayerPage(url: String, page: String): Player = {
    val lines = attributeLines(page)
    val values = lines.map(attributeValue)
    // get all the details
    val name = getSingleStringValue(NAME_REGEX, page, url)
    val theme = getSingleStringValue(THEME_REGEX, page, url)
    val position = parsePosition(cleanPositionString(getSingleStringValue(POSITION_REGEX, page, url)))
    val height = heightInches(getSingleStringValue(HEIGHT_REGEX, page, url))
    val weight = weightLbs(getSingleStringValue(WEIGHT_REGEX, page, url))
    val overall = getSingleStringValue(OVERALL_REGEX, page, url).toInt
    val id = generateId(url, name, theme, overall)
    val builder = new PlayerBuilder(
      id = id,
      name = name,
      position = position,
      overall = overall,
      height = height,
      weight = weight
    )
    builder.attributes ++= builder.buildAttributes(values.toIterator)
    builder
  }

  def getSingleStringValue(regex: Regex, page: String, url: String): String = regex.findFirstIn(page) match {
    case Some(regex(value)) => Io.stripHtml(value)
    case None =>
      println("================================================================")
      println(page)
      println("================================================================")
      throw new Exception("No value " + regex + " for that player (" + url + ").")
  }

  def attributeValue(line: String): Int = line match {
    case ATTRIBUTE_REGEX(original, duo, default, attributeName) => default.toInt
    case _ => throw new Exception("Don't try to use that method on bad strings.")
  }

  def attributeLines(page: String): Seq[String] = {
    ATTRIBUTE_REGEX.findAllIn(page).toSeq
  }

  def parsePosition(positionString: String): Position = {
    if (positionString.startsWith("PG"))
      PointGuard
    else if (positionString.startsWith("SG"))
      ShootingGuard
    else if (positionString.startsWith("SF"))
      SmallForward
    else if (positionString.startsWith("PF"))
      PowerForward
    else if (positionString.startsWith("C"))
      Center
    else throw new Exception("Unparsable position " + positionString)
  }

  val POSITION_CHARACTERS = "PGSFC"
  def cleanPositionString(positionString: String): String = {
    positionString.filter(c => POSITION_CHARACTERS.contains(c))
  }

  def heightInches(heightString: String): Int = heightString match {
    case FEET_INCHES_REGEX(feet, inches) =>
      feet.toInt * 12 + inches.toInt
    case _ => throw new Exception("Weird height format for: " + heightString)
  }

  def weightLbs(weightString: String): Int = weightString match {
    case LBS_REGEX(lbs) => lbs.toInt
    case _ => throw new Exception("Weird weight format for: " + weightString)
  }

  def generateId(url: String, name: String, theme: String, overall: Int): String = url match {
    case URL_REGEX(id) => id
    case _ => PlayerIo.pathSafeName(name) + theme + overall
  }
}
