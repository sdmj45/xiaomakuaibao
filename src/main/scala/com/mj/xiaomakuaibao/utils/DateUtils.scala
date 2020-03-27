package com.mj.xiaomakuaibao.utils

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneId}
import java.util.Date

import scala.util.Try

object DateUtils {
  private val formatterPattern = "yyyy-MM-dd'T'HH:mm:ss"
  val formatter: SimpleDateFormat = new SimpleDateFormat(
    formatterPattern
  )

  def convertDateToLocalDatetime(date: Date): LocalDateTime = {
    date.toInstant.atZone(ZoneId.systemDefault()).toLocalDateTime
  }

  def convertStrToLocalDatetime(str: String): Try[LocalDateTime] = {
    Try(formatter.parse(str)).map(convertDateToLocalDatetime)
  }


  def convertLocalDatetimeToStr(date: LocalDateTime): String = {
    date.format(DateTimeFormatter.ofPattern(formatterPattern))
  }
}
