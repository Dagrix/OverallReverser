package overallreverser

import overallreverser.parsing.TwoKMTCentralLister
import overallreverser.tools.Io

/** Crawl all the player URLs on 2KMTCentral
  * Created by dagrix on 25/01/2017.
  */
object PlayerUrlLister extends App {
  val central = new TwoKMTCentralLister()
  val allPlayerUrls = central.playerUrls()
  Io.writeLines(allPlayerUrls, "./data/playerUrls.txt")
}
