SHELL := /bin/bash

.PHONY: download-survey
download-survey:
	@echo "Downloading Stack Overflow Survey Data from https://insights.stackoverflow.com/survey"
	@mkdir -p data
	@-rm -rf data/stackoverflow_survey_2020 2> /dev/null
	@curl -L "https://drive.google.com/uc?export=download&id=1dfGerWeWkcyQ9GX9x20rdSGj7WtEpzBB" -o data/stackoverflow_survey_2020.zip
	@unzip -o data/stackoverflow_survey_2020.zip -d data/stackoverflow_survey_2020
	@rm data/stackoverflow_survey_2020.zip 2> /dev/null

.PHONY: download-stock
download-stock: guard-APIKEY
	@echo "Downloading Stock Data from https://www.alphavantage.co/"
	@stocks=(TSLA IBM AAPL ASML);\
	for stock in $${stocks[@]}; do \
	  for year in {1..2}; do \
	  	for month in {1..12}; do \
	  	  	offsetYear=$$(($$year-1));\
	  	  	offsetMonth=$$(($$month-1));\
  	  		slice=year$${year}month$${month};\
  	  		path=data/stocks/stock=$${stock}/offsetYear=$${offsetYear}/offsetMonth=$${offsetMonth}/$${stock}-$${slice}.csv;\
  	  		if [ ! -f $${path} ] || grep -q "Thank you for using Alpha Vantage" $${path}; then \
				mkdir -p $${path};\
				echo "Downloading $${stock} slice $${slice}} to $${path}";\
				curl -s -L "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY_EXTENDED&symbol=$${stock}&interval=5min&slice=$${slice}&adjusted=false&apikey=$${APIKEY}" -o $${path}; \
	  			sleep 12;\
	  		else\
	  			echo "Already downloaded $${stock} slice $${slice}} to $${path}";\
			fi;\
	  	done;\
	  done;\
	done;

.PHONY: stream-stocks
stream-stocks:
	@function slowcat(){ while read; do sleep .7; echo "$$REPLY"; done; };\
	cat  data/stocks_for_streaming/part-00000-f41ef6a1-53c0-4a8d-9e22-6f66b2694892-c000.json  | slowcat | ncat -lk 8888


.PHONY: unzip-data
unzip-data:
	@unzip data.zip

.PHONY: build-jar
build-jar:
	sbt assembly

.PHONY: run-trainer
run-trainer:
	@logfile=`pwd`/src/main/resources/log4j.properties;\
	spark-submit \
			--conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file://$$logfile" \
			--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=file://$$logfile" \
			--class part_05.SparkModelTrainer \
			output/scala-spark-course-assembly-0.1.0.jar \
			data/stocks_for_streaming data/stocks_model


.PHONY: run-predictor
run-predictor:
	@logfile=`pwd`/src/main/resources/log4j.properties;\
	spark-submit \
			--conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file://$$logfile" \
			--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=file://$$logfile" \
			--class part_05.SparkModelRunner \
			output/scala-spark-course-assembly-0.1.0.jar \
			data/stocks_for_streaming data/stocks_model


# Guard to check ENV vars
guard-%:
	@ if [ -z '${${*}}' ]; then echo 'Environment variable $* not set.' && exit 1; fi

# Catch all for module name arguments
%:
	@:
