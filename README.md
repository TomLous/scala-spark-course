# Spark Scala Course

Introduction course to Spark 3, using Scala

### Getting started

- (Optional) Install Spark 3.x locally
  - <https://spark.apache.org/downloads.html>
  - or mac: `brew install apache-spark`
- Unzip data

### Start streaming to socket

_Listens on port 8888. 
Change in Makefile & `part_04.StructuredStreaming` if already in use._

Run `make stream-stocks` 

It will send a stock each 0.7 seconds
Each stock record is a window of 5 minutes, and there are 3-4 stocks per window. 
So about 5 min of stocks per 2.8 seconds.

### Build jar
`make build-jar` stores it in the output folder


### Run Trainer Job

`make run-trainer` Does the spark-submit with all correct properties 


### Run Predictor Job

First finish the code :-)  

`make run-predictor` Does the spark-submit with all correct properties


### Manually download raw data

- `make download-survey`
- `APIKEY=???? make download-stock` get APIKEY from <https://www.alphavantage.co/>
- `sbt runMain part_03.DatasetForStreaming` to generate json

