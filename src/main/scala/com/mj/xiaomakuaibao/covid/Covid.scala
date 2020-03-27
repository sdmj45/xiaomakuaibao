package com.mj.xiaomakuaibao.covid

import java.text.SimpleDateFormat
import java.time.LocalDateTime

import better.files
import com.github.tototoshi.csv._
import com.mj.xiaomakuaibao.utils.DateUtils.{convertLocalDatetimeToStr, _}
import com.mj.xiaomakuaibao.utils.FileUtils.{overwriteToFile, _}
import com.mj.xiaomakuaibao.utils.{DateUtils, FileUtils}
import org.json4s.jackson.JsonMethods.parse
import org.json4s.{DefaultFormats, _}
import sttp.client.{HttpURLConnectionBackend, basicRequest, _}

import scala.collection.immutable
import scala.util.{Failure, Try}

object Covid {
  implicit val formats = new DefaultFormats {
    override def dateFormatter: SimpleDateFormat = formatter
  }

  val franceRegionMap: Map[String, String] = Map(
    "fr-bre" -> "Bretagne",
    "fr-pdl" -> "Pays de la Loire",
    "fr-pac" -> "Provence-Alpes-Côte d'Azur",
    "fr-occ" -> "Occitanie",
    "fr-naq" -> "Nouvelle-Aquitaine",
    "fr-bfc" -> "Bourgogne-Franche-Comté",
    "fr-cvl" -> "Centre-Val de Loire",
    "fr-idf" -> "Île-de-France",
    "fr-hdf" -> "Hauts-de-France",
    "fr-ara" -> "Auvergne-Rhône-Alpes",
    "fr-ges" -> "Grand Est",
    "fr-nor" -> "Normandie"
  )

  def main(args: Array[String]): Unit = {
    val lastUpdatedFile = "data/last_updated/covid_last_updated.txt"
    for {
      lastUpdatedTime <- readFile(lastUpdatedFile)
      lastCommitTime <- getLastCommitTime
      if ifToDownload(lastCommitTime, lastUpdatedTime)
    } {
      downloadFiles
      overwriteToFile(lastUpdatedFile, Some(convertLocalDatetimeToStr(lastCommitTime)))
      updateJsDataFiles
    }
  }

  private def updateJsDataFiles: Option[files.File] = {
    val confirmedInitialData = readInitialData("data/data/confirmed.csv")
    val deathsInitialData = readInitialData("data/data/deaths.csv")
    val recoveredInitialData = readInitialData("data/data/recovered.csv")

    val newCaseStr: String = generateNewCaseStr(confirmedInitialData)
    overwriteToFile("public/assets/js/data/dailyNewCase_data.js", Some(newCaseStr))

    val totalCaseStr: String = generateAllTotalCaseStr(Seq(("累计确诊", confirmedInitialData), ("累计死亡", deathsInitialData), ("累计治愈", recoveredInitialData)))
    overwriteToFile("public/assets/js/data/totalStat_data.js", Some(totalCaseStr))

    val mapStr: String = generateMapStr(confirmedInitialData)
    overwriteToFile("public/assets/js/data/map_data.js", Some(mapStr))
  }

  private def ifToDownload(lastCommitTime: LocalDateTime, lastUpdatedTime: LocalDateTime): Boolean = lastCommitTime.isAfter(lastUpdatedTime)

  private def getLastCommitTime: Try[LocalDateTime] = {
    implicit val backend = HttpURLConnectionBackend()
    val body = basicRequest
      .get(
        uri"https://api.github.com/search/commits?q=repo:cedricguadalupe/FRANCE-COVID-19+CGU - Update 2020-"
      )
      .header("Accept", "application/vnd.github.cloak-preview")
      .send()
      .body

    body.fold(
      e => Failure(new Throwable(e)),
      r => {
        val jValue: JValue = parse(r)
        val lastCommitTimeStr = ((jValue \ "items") (0) \ "commit" \ "committer" \ "date").values.toString
        DateUtils.convertStrToLocalDatetime(lastCommitTimeStr)
      }
    )
  }

  private def downloadFiles: immutable.Iterable[String] = {
    val filesToDownload = Map(
      "https://raw.githubusercontent.com/cedricguadalupe/FRANCE-COVID-19/master/france_coronavirus_time_series-confirmed.csv" -> "data/data/confirmed.csv",
      "https://raw.githubusercontent.com/cedricguadalupe/FRANCE-COVID-19/master/france_coronavirus_time_series-deaths.csv" -> "data/data/deaths.csv",
      "https://raw.githubusercontent.com/cedricguadalupe/FRANCE-COVID-19/master/france_coronavirus_time_series-recovered.csv" -> "data/data/recovered.csv"
    )
    FileUtils.downloadFiles(filesToDownload)
  }

  private def generateMapStr(initialData: immutable.Seq[Map[String, String]]): String = {
    val lastData = initialData.last
    val mapData = franceRegionMap.map { case (k, v) => s"""['$k', ${lastData(v).toInt}]""" }
    s"""var mapData=[${mapData.mkString(",")}]"""
  }

  private def generateAllTotalCaseStr(seq: Seq[(String, immutable.Seq[Map[String, String]])]): String = {
    val dataStr = seq.map { case (name, data) => generateSubTotalCaseStr(name, data) }.mkString(",")
    s"""var totalRecapData=[$dataStr]"""
  }

  private def generateSubTotalCaseStr(name: String, initialData: immutable.Seq[Map[String, String]]): String = {
    val data = initialData.map(map => {
      val Array(day, month, year) = map("Date").split("/")
      val total = map("Total").toInt
      createNewLine(year, month, day, total)
    })
    s"""{name:'$name',data:[${data.mkString(",")}]}"""
  }

  private def generateNewCaseStr(initialData: immutable.Seq[Map[String, String]]) = {
    val endCases = initialData.sliding(2).map { case List(x, y) => {
      val Array(day, month, year) = y("Date").split("/")
      val diff = y("Total").toInt - x("Total").toInt
      createNewLine(year, month, day, diff)
    }
    }.toList

    val csvHead = initialData.head
    val Array(day, month, year) = csvHead("Date").split("/")
    val csvHeadCase = createNewLine(year, month, day, csvHead("Total").toInt)

    val newCaseList = csvHeadCase :: endCases
    s"""var dailyNewCaseData=[${newCaseList.mkString(",")}]"""
  }

  private def readInitialData(filePath: String): immutable.Seq[Map[String, String]] = {
    val reader = CSVReader.open(toJFile(filePath))
    reader.allWithHeaders()
  }

  private def createNewLine(year: String, month: String, day: String, diff: Int): String = {
    s"""[Date.UTC($year, ${month.toInt - 1},$day), $diff]"""
  }

}
