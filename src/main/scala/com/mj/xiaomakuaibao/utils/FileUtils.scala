package com.mj.xiaomakuaibao.utils

import java.io.{File => JFile}
import java.net.URL
import java.time.LocalDateTime

import better.files.{File, _}

import scala.collection.immutable
import scala.sys.process._
import scala.util.Try

object FileUtils {

  def readFile(fileName: String): Try[LocalDateTime] = {
    for {
      str <- Try(fileName.toFile).map(_.contentAsString)
      res <- DateUtils.convertStrToLocalDatetime(str)
    } yield res
  }

  def toJFile(fileName: String): JFile = {
    new JFile(fileName.toFile.uri)
  }

  def downloadFile(url: String, filename: String): String = {
    new URL(url) #> new JFile(filename) !!
  }

  def downloadFiles(files: Map[String, String]): immutable.Iterable[String] = {
    files.map { case (url, filename) => FileUtils.downloadFile(url, filename) }
  }

  def appendToFile(fileName: String, content: Option[String]): Option[File] = {
    content.map(c => {
      val f: File = fileName.toFile
      f.append(c)
    })
  }

  def overwriteToFile(fileName: String,
                      content: Option[String]): Option[File] = {
    content.map(c => {
      val f: File = fileName.toFile
      f.overwrite(c)
    })
  }
}
