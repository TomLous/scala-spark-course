package part_01

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object PairRDD extends App{

  def halt():Unit = {
    println("> ")
    System.in.read
  }


  val spark = SparkSession
    .builder()
    .appName("PairRDD")
    .config("spark.master", "local[*]")
    .getOrCreate()


  val rdd:RDD[Int] = spark.sparkContext.parallelize((0 to 1000), 4)

  val keyValueRDD:RDD[(String, Int)] = rdd.keyBy(n => if (n % 2 == 0) "even" else "odd")

  val keys:RDD[String] = keyValueRDD.keys.distinct
  println("Num distinct keys: " + keys.collect().length)   // <- RDDTest action


  halt


  val rddValues:RDD[String] = spark.sparkContext.parallelize(List("dog","cat","gnu","salmon","rabbit"," turkey","wolf","bear","bee"), 3)
  val rddKeys:RDD[Int] = spark.sparkContext.parallelize(List(1,1,2,2,2,1,2,2,2), 3)

  val rddPaired:RDD[(Int, String)] = rddKeys.zip(rddValues)

  val rddCombined:RDD[(Int, List[String])] = rddPaired.combineByKey(
    str => List(str), // createCombiner
    (l: List[String], str:String) =>  str :: l, // mergeValue
    (lA: List[String], lB: List[String]) => lA ::: lB // mergeCombiners
  )

  rddCombined.foreach(println) // <- RDDTest action

  halt





}
