package part_00

import org.apache.spark.sql.{DataFrame, SparkSession}

object JobsContext extends App {

  def halt():Unit = {
    println("> ")
    System.in.read
  }


  val spark = SparkSession
    .builder()
    .appName("JobsContext")
    .config("spark.master", "local[*]")
    .getOrCreate()

  import spark.implicits._

  val dataFrame = spark
    .read
    .option("header", true)
    .option("sep", ",")
    .csv("data/stackoverflow_survey_2020/survey_results_public.csv") // <- this is not evaluated yet, but it still needs a job to read meta

  println("Defined the source")
  halt

  dataFrame.show() // <- now it gets evaluated (action) => Job

  halt

  dataFrame.printSchema() // <- not an action. Reads meta info

  halt

  println("Number of records " + dataFrame.count()) // <- now it gets evaluated again (action) => Job

  halt

  val dummyDataFrame = (0 until 100)
    .map(num => (num, s"Some Field $num"))
    .toDF("number", "random")  //

  val joinedDataFrame = dataFrame.crossJoin(dummyDataFrame) // <- this is not evaluated yet

  println("Defined the join")
  halt

  println("Number of records " + joinedDataFrame.count()) // <- now it gets evaluated again (action) => Job

  println("Done")
  halt
}
