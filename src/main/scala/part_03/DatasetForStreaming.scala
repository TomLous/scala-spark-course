package part_03

import org.apache.spark.sql.{Dataset, Encoders, SparkSession}
import part_03.DatasetExample.halt
import part_03.model.Stock

object DatasetForStreaming extends App{

  val spark = SparkSession
    .builder()
    .appName("DataFrameBasics")
    .config("spark.master", "local[*]")
    .getOrCreate()

  import spark.implicits._


  val schema = Encoders.product[Stock].schema // what does the schema of this class look like?

  spark
    .read
    .option("header", true)
    .option("sep", ",")
    .schema(schema) // <- tell the csv reader what to expect
    .csv("data/stocks")
    .as[Stock]
    .drop("offsetYear", "offsetMonth")
    .orderBy('time)
    .repartition(1)
    .write
    .format("json")
    .save("data/stocks_for_streaming")



}
