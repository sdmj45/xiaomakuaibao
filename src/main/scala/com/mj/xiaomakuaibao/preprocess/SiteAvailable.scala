package com.mj.xiaomakuaibao.preprocess

import org.apache.spark.sql.{DataFrame, SparkSession}

object SiteAvailable {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "C:\\Dev\\renault\\projects\\winutils")

    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("Spark pre process site available")
      .getOrCreate()

    import spark.implicits._

    val initial: DataFrame = spark.read
      .options(Map("inferSchema" -> "true", "delimiter" -> ",", "header" -> "true"))
      .csv("src/main/resources/preprocess/poi_osm.csv")
    val df = initial.drop($"wikidata")
      .drop($"infos")
      .drop($"osm_id")
      .drop($"url_hours")
      .filter($"status" !== "inconnu")
      .filter($"status" !== "fermé")

    df
      .coalesce(1)
      .write.format("csv")
      .option("header", "true")
      .mode("overwrite")
      .save("src/main/resources/preprocess/new_osm.csv")
  }

}
