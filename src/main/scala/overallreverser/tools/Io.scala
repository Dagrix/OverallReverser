package overallreverser.tools

import java.io.{BufferedReader, BufferedWriter, FileWriter, InputStreamReader}
import java.net.{HttpURLConnection, URL, URLEncoder}

import org.apache.commons.io.IOUtils
import java.nio.charset.StandardCharsets

import scala.concurrent.Future
import scala.io.Source

/**
  * Handles basic IO operations (download a page, read/write lines in a file)
  * Created by dagrix on 25/01/2017.
  */
object Io {

  def downloadPage(url: String): String = {
    IOUtils.toString(new URL(url), StandardCharsets.UTF_8)
  }

  def downloadPage(url: String, timeoutMs: Int): String = {
    val sb = new StringBuilder()
    val urlconn = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
    urlconn.setRequestMethod("GET")
    urlconn.setRequestProperty("Connection", "keep-alive")
    urlconn.setReadTimeout(timeoutMs)
    urlconn.setInstanceFollowRedirects(true)
//
//    Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
//    Accept-Encoding:gzip, deflate, sdch
//    Accept-Language:en-US,en;q=0.8,fr-FR;q=0.6,fr;q=0.4
//    Connection:keep-alive
//    Cookie:cookieconsent_dismissed=yes; _bcma=743451542; cdmu=1481545705783; bknx_fa=1481545706079; show_hidden=false; remember_key=987eb7185469996b925f4d20c545e9952afd9519ce0735cf70a3503fc33bd63dc4c5f6e7d9975c7cccaf4461e89620131efc7976037e0dd0908f04cdc42f08c1LBtySBdfBexciQMyz8%2FLlUg4vZ9%2FBuZwgVNc%2BJCZ5nn7fHHhfZ%2FMxYUimQpsjOSQuEhA1vQF3A3DjE1lDwGShHieb2p3n24iFuxjdT1mhJnYn%2Bk79CiZe%2FN0zXjyW3NbXi%2BIEeiJUFBY5%2F9ujTgEIAh%2Bdif8B%2BCdmlkNwklNEGTwcO5sOtqa7JBE%2FNOJjsr2zXhGZLr7Wkfeu3TJFfyVHqEKaHwzbwEU6YY%2BzPX9ORU%3D; ci_session=962a49dee3fdfef971242167baedfeddfe72d64d; _gat=1; bknx_ss=1487179570779; _ga=GA1.2.639222859.1476795653; cdmblk2=0:0:0:0:0:0:0:0:0:0:0:0:0,0:0:0:0:0:0:0:0:0:0:0:0:0,0:0:0:0:0:0:0:0:0:0:0:0:0,0:0:0:0:0:0:0:0:0:0:0:0:0,0:0:0:0:0:0:0:0:0:0:0:0:0,0:0:0:0:0:0:0:0:0:0:0:0:0; cdmabp=true; cdmblk=0:0:0:0:0:0:0:0:0:0:0:0:0,0:0:0:0:0:0:0:0:0:0:0:0:0,0:0:0:0:0:0:0:0:0:0:0:0:0,0:0:0:0:0:0:0:0:0:0:0:0:0,0:0:0:0:0:0:0:0:0:0:0:0:0,0:0:0:0:0:0:0:0:0:0:0:0:0; cdmtlk=0:0:0:0:0:0:0:0:0:0:0:0:0; cdmgeo=eu; cdmbaserate=2.1; cdmbaseraterow=1.1; cdmint=0
//    Host:2kmtcentral.com
//    Referer:http://2kmtcentral.com/
//      Upgrade-Insecure-Requests:1
//    User-Agent:Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36
//
    urlconn.setRequestMethod("GET")
    urlconn.connect()
    val buf = new BufferedReader(new InputStreamReader(urlconn.getInputStream))
    var line = ""
    while({line = buf.readLine(); line} != null) {
      sb.append(line)
      sb.append("\n")
    }
    sb.toString()
  }

  def downloadPage(url: String, timeoutMs: Int, delayBeforeRetryMs: Int, numberOfTries: Int): String = {
    var response = ""
    var tries = 0
    while (response.isEmpty && tries < numberOfTries) {
      response = downloadPage(url, timeoutMs)
      if (response.isEmpty)
        Thread.sleep(delayBeforeRetryMs)
      tries += 1
    }
    response
  }

  def writeLines(lines: Seq[String], path: String): Unit = {
    val bw = new BufferedWriter(new FileWriter(path))
    lines.foreach { line =>
      bw.write(line)
      bw.newLine()
    }
    bw.close()
  }

  def readLines(path: String): Seq[String] = {
    val source = Source.fromFile(path)
    // toSeq is lazy on iterators, you need one of toVector, toList or toArray to force the getLines to complete
    val res = source.getLines().toVector
    source.close()
    res
  }

  def lastPartEncode(url: String): String = {
    val splitsReversed = url.split("/").reverse
    val fixed = URLEncoder.encode(splitsReversed.head, "UTF-8") +: splitsReversed.tail
    fixed.reverse.mkString("/")
  }

  def stripHtml(s: String): String = {
    try {
      scala.xml.XML.loadString(s).text
    } catch {
      case _: org.xml.sax.SAXParseException =>
        // this happens when 's' is a non-XML string, i.e. already clean
        s
    }
  }
}
