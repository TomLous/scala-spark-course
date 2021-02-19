package part_04.model

import java.sql.Timestamp

case class Stock (
                  stock:String,
                  time: Timestamp,
                  open:Double,
                  high:Double,
                  low:Double,
                  close:Double,
                  volume:BigInt
                )
