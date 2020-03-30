package com.mj.xiaomakuaibao.preprocess

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.row_number
import org.apache.spark.sql.functions._

object Rumor {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "C:\\Dev\\renault\\projects\\winutils")

    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("Spark pre process Rumor")
      .getOrCreate()

    import spark.implicits._

    val initial: DataFrame = spark.read
      .options(Map("inferSchema" -> "true", "delimiter" -> ",", "header" -> "true", "multiLine" -> "true"))
      .csv("src/main/resources/preprocess/DXYRumors.csv")


    val windowSpec = Window.partitionBy("_id").orderBy($"_id".desc)

    val df = initial.select($"title", $"mainSummary", $"body")
      .dropDuplicates("title")
      .filter(!$"title".contains("北京"))
      .filter(!$"title".contains("上海"))
      .filter(!$"title".contains("武汉"))
      .filter(!$"title".contains("日本"))
      .filter(!$"title".contains("深圳"))
      .filter(!$"title".contains("西宁"))
      .filter(!$"title".contains("美国"))
      .filter(!$"title".contains("南京"))
      .filter(!$"title".contains("钟南山"))
      .filter(!$"title".contains("黑龙江"))
      .filter(!$"title".contains("华南"))
      .filter(!$"title".contains("烟花"))
      .filter(!$"title".contains("青岛"))
      .filter(!$"title".contains("长沙"))
      .filter(!$"title".contains("十堰"))
      .filter(!$"title".contains("海关"))
      .filter(!$"title".contains("中国"))
      .filter(!$"title".contains("印度"))
      .filter(!$"title".contains("SARS"))
      .filter(!$"title".contains("俄罗斯"))
      .filter(!$"title".contains("sk5"))
      .filter(!$"title".contains("温州"))
      .filter(!$"title".contains("猪崽"))
      .filter(!$"title".contains("丁香"))
      .filter(!$"title".contains("城市"))
      .filter(!$"title".contains("山西"))
      .filter(!$"title".contains("泰签"))
      .filter(!$"title".contains("沪援鄂"))
      .filter(!$"title".contains("杭州"))
      .filter(!$"title".contains("2018"))
      .filter(!$"title".contains("解放军"))
      .filter(!$"title".contains("10万"))
      .filter(!$"title".contains("玩雪"))
      .filter(!$"title".contains("鱼塘"))
      .filter(!$"title".contains("牛蛙"))
      .filter(!$"title".contains("SARI"))
      .withColumn("row_number", monotonically_increasing_id)

    df.printSchema()

    df.map(r=>{
      s"""<li data-aos="fade-up" data-aos-delay="500">
      <a data-toggle="collapse" href="#rumor${r.getLong(3)}" class="collapsed">${r.getString(0)}<i
      class="icofont-simple-up"></i></a>
      <div id="rumor${r.getLong(3)}" class="collapse" data-parent=".faq-list">
        <p>
          ${r.getString(1)}<br>
          ${r.getString(2)}
        </p>
        </div>
      </li>"""
    }).coalesce(1)
      .write.format("text")
      .option("header", "true")
      .mode("overwrite")
      .save("src/main/resources/preprocess/new_rumor.txt")


  }

}
