package com.mj.xiaomakuaibao.utils

import java.text.SimpleDateFormat
import java.time.{LocalDateTime, ZoneId}
import java.util.Date

object DateUtils {
  val formatter: SimpleDateFormat = new SimpleDateFormat(
    "yyyy-MM-dd'T'HH:mm:ss"
  )
  def convertDateToLocalDatetime(date: Date): LocalDateTime = {
    date.toInstant.atZone(ZoneId.systemDefault()).toLocalDateTime
  }
}
