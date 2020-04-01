package com.mj.xiaomakuaibao.covid

import java.text.SimpleDateFormat
import java.time.LocalDateTime

import better.files
import com.github.tototoshi.csv._
import com.mj.xiaomakuaibao.Module
import com.mj.xiaomakuaibao.utils.DateUtils.{convertLocalDatetimeToStr, _}
import com.mj.xiaomakuaibao.utils.FileUtils.{overwriteToFile, _}
import com.mj.xiaomakuaibao.utils.{DateUtils, FileUtils}
import org.json4s.jackson.JsonMethods.parse
import org.json4s.{DefaultFormats, _}
import sttp.client.{HttpURLConnectionBackend, basicRequest, _}

import scala.collection.immutable
import scala.util.{Failure, Try}

class Covid extends Module {
  override val lastUpdatedFile: String = "data/last_updated/covid_last_updated.txt"

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

  override def run: Unit = {
    for {
      lastUpdatedTime <- readFile(lastUpdatedFile)
      lastCommitTime <- getLastCommitTime
      if ifToDownload(lastCommitTime, lastUpdatedTime)
    } {
      //downloadFiles
      overwriteToFile(lastUpdatedFile, Some(convertLocalDatetimeToStr(lastCommitTime)))
      updateDataFiles
    }
    println("Covid ok !")
  }

  private def updateDataFiles: Option[files.File] = {
    val confirmedInitialData = readInitialData("data/data/confirmed.csv")
    val deathsInitialData = readInitialData("data/data/deaths.csv")
    val recoveredInitialData = readInitialData("data/data/recovered.csv")

    val newConfirmedCaseStr: String = calculateEachDayDiffStr(confirmedInitialData)
    val newDeathsCaseStr: String = calculateEachDayDiffStr(deathsInitialData)
    val newRecoveredCaseStr: String = calculateEachDayDiffStr(recoveredInitialData)

    val summaryData = Seq(("法国累计确诊", "text-primary", confirmedInitialData), ("法国累计死亡", "text-danger", deathsInitialData), ("法国累计治愈", "text-success", recoveredInitialData))
    val allTotalCaseData = Seq(("累计确诊", confirmedInitialData), ("累计死亡", deathsInitialData), ("累计治愈", recoveredInitialData))
    val allDiffDataStr: Seq[(String, String)] = Seq(("每日新增确诊", newConfirmedCaseStr), ("每日新增死亡", newDeathsCaseStr), ("每日新增治愈", newRecoveredCaseStr))

    val summaryStr: String = generateSummary(summaryData)
    overwriteToFile("public/assets/data/summary.html", Some(summaryStr))

    val totalCaseEachDayStr: String = generateAllTotalCaseStr(allTotalCaseData)
    overwriteToFile("public/assets/data/totalCaseEachDayData.js", Some(totalCaseEachDayStr))

    val diffRecapData = generateDiffCaseEachDayStr(allDiffDataStr)
    overwriteToFile("public/assets/data/diffCaseEachDayData.js", Some(diffRecapData))

    overwriteToFile("public/assets/data/dailyNewCaseData.js", Some(s"""var dailyNewCaseData=[$newConfirmedCaseStr]"""))

    val mapStr: String = generateMapStr(confirmedInitialData)
    overwriteToFile("public/assets/data/mapData.js", Some(mapStr))
  }

  private def ifToDownload(lastCommitTime: LocalDateTime, lastUpdatedTime: LocalDateTime): Boolean = lastCommitTime.isAfter(lastUpdatedTime)

  private def getLastCommitTime: Try[LocalDateTime] = {
    implicit val backend = HttpURLConnectionBackend()
    val body = basicRequest
      .get(
        uri"https://api.github.com/search/commits?q=repo:cedricguadalupe/FRANCE-COVID-19+CGU - Maj Geodes %26 Update 2020"
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

  private def generateDiffCaseEachDayStr(allDiffDataStr: Seq[(String, String)]): String = {
    val prepare = allDiffDataStr.map { case (name, data) => s"""{name:'$name',data:[$data]}""" }
    s"""var diffRecapData=[${prepare.mkString(",")}]"""
  }

  private def generateMapStr(initialData: immutable.Seq[Map[String, String]]): String = {
    val lastData = initialData.last
    val mapData = franceRegionMap.map { case (k, v) => s"""['$k', ${lastData(v).toInt}]""" }
    s"""var mapData=[${mapData.mkString(",")}]"""
  }

  private def generateSummary(seq: Seq[(String, String, immutable.Seq[Map[String, String]])]): String = {
    seq.map { case (name, className, data) => generateSubSummaryStr(name, className, data) }.mkString("")
  }

  private def generateAllTotalCaseStr(seq: Seq[(String, immutable.Seq[Map[String, String]])]): String = {
    val dataStr = seq.map { case (name, data) => generateSubTotalCaseEachDayStr(name, data) }.mkString(",")
    s"""var totalRecapData=[$dataStr]"""
  }

  private def generateSubSummaryStr(name: String, className: String, initialData: immutable.Seq[Map[String, String]]): String = {
    val beforeLast :: last :: Nil = initialData.takeRight(2)
    val total = last("Total").toInt
    val diff = total - beforeLast("Total").toInt

    s"""<div class="col-12 col-lg-6 col-xl"> <div class="card"> <div class="card-body"> <div class="row align-items-center"> <div class="col"> <h6 class="text-uppercase text-muted mb-2">$name</h6> <span class="h2 mb-0 font-weight-bold $className">$total</span> <dev class="badge badge-soft-success mt-n1">较昨日 <span class="badge badge-info">+$diff</span></dev> </div></div></div></div></div>"""
  }

  private def generateSubTotalCaseEachDayStr(name: String, initialData: immutable.Seq[Map[String, String]]): String = {
    val data = initialData.map(map => {
      val Array(day, month, year) = map("Date").split("/")
      val total = map("Total").toInt
      createNewLine(year, month, day, total)
    })
    s"""{name:'$name',data:[${data.mkString(",")}]}"""
  }

  private def calculateEachDayDiffStr(initialData: immutable.Seq[Map[String, String]]): String = {
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
    newCaseList.mkString(",")
  }

  private def readInitialData(filePath: String): immutable.Seq[Map[String, String]] = {
    val reader: CSVReader = CSVReader.open(toJFile(filePath))
    reader.allWithHeaders()
  }

  private def createNewLine(year: String, month: String, day: String, num: Int): String = {
    s"""[Date.UTC($year, ${month.toInt - 1},$day), $num]"""
  }


}
