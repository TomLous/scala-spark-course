package part_05

import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder, TrainValidationSplit}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.streaming.Durations.minutes
import part_04.model.Stock
import part_05.ml.StockTransformer

object SparkModelTrainer extends App {

  // Loggers are superior to println, this way you can decide verbosity at runtime and which package level
  @transient implicit val logger: Logger = Logger.getLogger(getClass.getName)

  // There are better libraries for handling input, but that's out of scope
  if(args.length != 2){
    logger.error("Expected [input-path] [output-path] cli params")
    System.exit(1)
  }

  val inputPath = args(0)
  val outputPath = args(1)

  implicit val spark = SparkSession
    .builder()
    .config(new SparkConf()
      .setIfMissing("spark.master", "local[*]")  // This way when running in any environment different then local that master is used
    )
    .appName("SparkModelTrainer")
    .getOrCreate()

  import spark.implicits._

  val lagSize = 20
  val useDataUntil =  "2020-01-01"



  val appleStockData = spark
      .read
      .json(inputPath)
      .withColumn("time",to_timestamp('time))
      .as[Stock]
      .filter('stock === "AAPL"  && 'time < useDataUntil)
      .transform(StockTransformer(lagSize))


  val linReg = new LinearRegression()
    .setMaxIter(10)

  val paramGrid = new ParamGridBuilder()
    .addGrid(linReg.regParam, Array(0.1, 0.01))
    .addGrid(linReg.fitIntercept)
    .addGrid(linReg.elasticNetParam, Array(0.0, 0.5, 1.0))
    .build()

  val Array(training, test) = appleStockData.randomSplit(Array(0.8, 0.2))


  val trainValidationSplit = new TrainValidationSplit()
    .setEstimator(linReg)
      .setEvaluator(new RegressionEvaluator)
    .setEstimatorParamMaps(paramGrid)
    // 80% of the data will be used for training and the remaining 20% for validation.
    .setTrainRatio(0.8)
    // Evaluate up to 2 parameter settings in parallel
    .setParallelism(2)

  val model = trainValidationSplit.fit(training)

  // Some output
  val predictions = model.transform(test)

  val evals = List("rmse", "r2").map(metric => {
      metric -> new RegressionEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName(metric)
        .evaluate(predictions)
  })

  evals.foreach(logger.info)

  model
    .write
    .overwrite()
    .save(outputPath)

}
