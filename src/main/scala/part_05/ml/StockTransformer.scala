package part_05.ml

import org.apache.log4j.Logger
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{collect_list, size}
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import part_04.model.Stock

case class StockTransformer(lagSize: Long)(implicit spark: SparkSession, logger: Logger) extends Function[Dataset[Stock], DataFrame] {


  override def apply(v1: Dataset[Stock]): DataFrame = {
    import spark.implicits._

    val lagWindow = Window.orderBy("tick").rowsBetween(-lagSize,-1)

    v1
      .map(s => (s.time,  s.close))
      .toDF("tick", "value")
      .withColumn("preceding", collect_list('value).over(lagWindow))
      .filter(size('preceding) === lagSize)
      .drop("tick")
      .as[(Double, List[Double])]
      .map(tuple => (tuple._1, Vectors.dense(tuple._2.toArray)))
      .toDF("label", "features")
  }

}
