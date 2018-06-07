package overallreverser

import overallreverser.parsing.{TwoKMTCentralLister, TwoKMTCentralPlayerParser}
import overallreverser.players.PlayerBuilder
import overallreverser.tools.{Io, PlayerIo}

/**
  * Created by dagrix on 25/01/2017.
  */
object PlayerDownloader extends App {

  val MAX_PLAYERS = Int.MaxValue

  // read the url list
  val allPlayerUrls = Io.readLines("./data/playerUrls.txt")
  // download the first MAX_PLAYERS of 2KMTCentral
  val first20 = allPlayerUrls.take(MAX_PLAYERS)
  val parser = new TwoKMTCentralPlayerParser
  first20.foreach { pageUrl =>
    Thread.sleep(5000)
    val page = Io.downloadPage(Io.lastPartEncode(pageUrl))
    val player = parser.parsePlayerPage(pageUrl, page)
    println(player)
    PlayerIo.writePlayer(player, "./data/players")
  }
}
