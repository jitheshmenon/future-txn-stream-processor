.PHONY: help

help:	## Description of commands available
	@cat $(MAKEFILE_LIST) | grep -e ":\s*##" | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

start-kafka-stack: ## Start the kafka stack
	docker-compose -f docker/docker-compose.yml up -d --build --remove-orphans

stop-kafka-stack: ## Terminate the kafka stack
	docker-compose -f docker/docker-compose.yml down

restart-kafka-stack: stop-kafka-stack start-kafka-stack

copy-data-files: ## Copy the data files from local to the container
	docker cp ./docker/data/Input.txt  broker:/data

connect-import: ## Import data from file to kafka topic
	docker exec -it broker connect-standalone /kafka-connect-config/connect-standalone.properties /kafka-connect-config/connect-file-source.properties

conect-reimport: copy-data-files connect-import

clean:	## Clean build target
	mvn clean

compile:
	mvn compile

build:	## Build target used by CI/CD
	mvn clean package

test: 		## Run test suite for service
	mvn test

verify: 	## Run integration tests
	mvn verify

run-local: ## Execute the Kafka stream processor locally
	mvn -Dspring.profiles.active=local spring-boot:run

test-api: ## Call the REST API
	curl --location --request GET 'http://localhost:8080/txn-amount' --header 'Content-Type: application/json'

kafdrop-local:
	docker run -d --rm -p 9990:9000 \
	   -e KAFKA_BROKERCONNECT=broker:9092 \
	   -e SCHEMAREGISTRY_CONNECT=http://schema-registry:8081 \
	   -e JVM_OPTS="-Xms32M -Xmx64M" \
	   -e SERVER_SERVLET_CONTEXTPATH="/" \
	   obsidiandynamics/kafdrop
