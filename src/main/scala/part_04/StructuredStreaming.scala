package part_04

import org.apache.spark.sql.{Encoders, SparkSession}
import org.apache.spark.sql.functions._
import part_04.model.Stock

object StructuredStreaming extends App {

  val spark = SparkSession
    .builder()
    .appName("DataFrameBasics")
    .config("spark.master", "local[*]")
    .getOrCreate()

  import spark.implicits._

  val stocks = spark
    .readStream
    .format("socket")
    .option("host", "localhost")
    .option("port", 8888)
    .load()
    .select(from_json($"value", Encoders.product[Stock].schema))
    .as[Tuple1[Stock]]
    .map(_._1)
    .as[Stock]

  stocks.printSchema()

    val windowedStocks = stocks
      .withWatermark("time", "60 minutes")
      .groupBy(
        window($"time", "40 minutes", "5 minutes"),
        $"stock"
      )
      .agg(
        count("volume").as("num"),
        first("open").as("opened"),
        last("close").as("closed"),
        max("high").as("highest"),
        min("low").as("lowest"),
        sum("volume").as("volume")
      )

  val query = windowedStocks.writeStream
      .outputMode("append")
      .option("truncate", false)
      .format("console")
      .start()

  query.awaitTermination()



}
