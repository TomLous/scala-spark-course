package part_03

import org.apache.spark.sql.{DataFrame, Dataset, Encoders, SparkSession}
import org.apache.spark.sql.functions._
import part_03.model.Stock

object DatasetExample extends App{



  def halt():Unit = {
    println("> ")
    System.in.read
  }


  val spark = SparkSession
    .builder()
    .appName("DataFrameBasics")
    .config("spark.master", "local[*]")
    .getOrCreate()

  import spark.implicits._

  // Show basic DataFrame Schema
  spark
    .read
    .option("header", true)
    .option("sep", ",")
    .csv("data/stocks")
    .printSchema()

  halt


  val schema = Encoders.product[Stock].schema // what does the schema of this class look like?

  val dataset:Dataset[Stock] = spark
    .read
    .option("header", true)
    .option("sep", ",")
    .schema(schema) // <- tell the csv reader what to expect
    .csv("data/stocks")
    .as[Stock]

  dataset.printSchema()

  halt

  dataset
    .map(_.diff) // <- call Stock methods
    .show()

  halt

  dataset
    .withColumn("date", $"time".cast("date"))
    .drop("offsetYear", "offsetMonth")
    .groupBy("date", "stock")
    .agg(
      max("high").as("highest"),
      min("low").as("lowest")
    )
    .show()

  halt

}
