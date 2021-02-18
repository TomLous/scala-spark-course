package part_02

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object DataFrameBasics extends App{

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


  val rdd:RDD[Row]  = dataFrame.rdd
  println(s"DataFrameBasics still contains an RDD of type Row with ${rdd.getNumPartitions} partitions")

  val schemaFromCSV:StructType = dataFrame.schema

  println(s"DataFrameBasics evaluated the csv to generate this schema with ${schemaFromCSV.fields.length} columns for the Row")

  // Find the field named "Country"
  val countryIndex =  schemaFromCSV.fieldIndex("Country")

  // Map over the dataframe to get the countries
  val countries = dataFrame
    .map(row => row.getString(countryIndex))
    .distinct()
    .filter(country => country.startsWith("N"))
    .show()

  halt()










}
