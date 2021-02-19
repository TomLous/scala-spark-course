package part_05

import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.ml.regression.{LinearRegression, LinearRegressionModel}
import org.apache.spark.sql.SparkSession

object SparkModelRunner extends App {

  // Loggers are superior to println, this way you can decide verbosity at runtime and which package level
  @transient implicit val logger: Logger = Logger.getLogger(getClass.getName)

  // There are better libraries for handling input, but that's out of scope
  if(args.length != 2){
    logger.error("Expected [input-path] [model-path] cli params")
    System.exit(1)
  }

  val inputPath = args(0)
  val modelPath = args(1)

  implicit val spark = SparkSession
    .builder()
    .config(new SparkConf()
      .setIfMissing("spark.master", "local[*]")  // This way when running in any environment different then local that master is used
    )
    .appName("SparkModelTrainer")
    .getOrCreate()

  import spark.implicits._

  val lagSize = 20

  val model = LinearRegressionModel.load(modelPath)

  model.transform(???)

  // TODO Build a Streaming Service that

}
