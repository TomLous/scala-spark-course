package part_02

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.{Column, DataFrame, Row, SparkSession}
import org.apache.spark.sql.types.StructType

object DataFrameSQL extends App{

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

  val dataFrame:DataFrame = spark
    .read
    .option("header", true)
    .option("sep", ",")
    .csv("data/stackoverflow_survey_2020/survey_results_public.csv")

  val countryCol:Column = $"Country"

  dataFrame
    .select(countryCol)
    .distinct()
    .where(countryCol.startsWith(lit("N")))
    .show() // <-- now look at the optimizer

  halt()

  dataFrame.createOrReplaceTempView("Survey")


  spark.sql("SELECT DISTINCT Country FROM Survey WHERE Country LIKE 'N%'").show()

  halt()

  // now do some int magic

  dataFrame
    .select(
      $"Age".cast("int").as("intAge"), // <- ugly
      $"YearsCode".cast("int").as("intYearsCode") // <- ugly
    )
    .filter($"intAge".isNotNull &&  $"intYearsCode".isNotNull)
    .show()

  // If only there was a way to type the fields...

}

