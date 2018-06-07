package overallreverser.parsing

import overallreverser.tools.Io

import scala.collection.mutable.ArrayBuffer

/**
  * Created by dagrix on 25/01/2017.
  */
class TwoKMTCentralLister(val path: String = "./temp/") {

  val PLAYER_ENTRY_PATTERN = "<a class=\"name box-link\" href=\"([^\"]*)\">".r

  def playerUrls(): Seq[String] = {
    val res = new ArrayBuffer[String]()
    var players = Seq[String]()
    var pageNumber = 0
    do {
      players = playerUrls(pageNumber)
      res ++= players
      pageNumber += 1
    } while (players.nonEmpty)
    res
  }

  def playerUrls(pageNumber: Int): Seq[String] = {
    val page = Io.downloadPage(TwoKMTCentralLister.PLAYER_LIST_URL.replace("$", pageNumber.toString))
    val allMatches = PLAYER_ENTRY_PATTERN.findAllIn(page)
    allMatches.map { case PLAYER_ENTRY_PATTERN(url) => url }.toSeq
  }
}

object TwoKMTCentralLister {
  val PLAYER_LIST_URL = "http://2kmtcentral.com/17/players/page/$" // the '$' is to be substituted with 0, 1, 2, 3...
}
