#Spark Scala Course

Introduction course to Spark 3, using Scala

## Get started

- (Optional) Install Spark 3.x locally
  - <https://spark.apache.org/downloads.html>
  - or mac: `brew install apache-spark`
- Unzip data

## Start streaming to socket

On port 8888. Change in Makefile & `paert_04.StructuredStreaming` if already in use.

`make stream-stocks` 

Will send a stock each 0.7 seconds
Each stock record is a window of 5 minutes, and there are 3-4 stocks per window. 
So about 5 min of stocks per 2.8 seconds.

  
## Manually download raw data

- `make download-survey`
- `APIKEY=???? make download-stock` get APIKEY from <https://www.alphavantage.co/>
- `sbt runMain part_03.DatasetForStreaming` to generate json

