package part_03.model

import java.sql.Timestamp

case class Stock (
                   stock:String,
                   time: Timestamp,
                   open:Double,
                   high:Double,
                   low:Double,
                   close:Double,
                   volume:Int,
                   offsetYear: Int,
                   offsetMonth: Int
                 ){
  lazy val diff:Double = open - close
}

