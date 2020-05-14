# Future Transaction Stream Processor

This microservice handles the processing of fixed width text data from System A and generates summary
tables for reporting and enables realtime retrieval of data through API calls.

## Overview ##
The project uses Docker containers to provision Kafka brokers and associated components
such as Zookeeper, Schema Registry and Postgres Database instances. The `Kafdrop` image
here enables an interactive UI for listing the topics and viewing messages inside topics.

## Environment Setup ##
The container stack for the project is scripted with `Makefile`. The entire stack can be 
 provisioned using the command `make start-kafka-stack`. This will pull the latest images 
 from docker repo and will provision the images locally. 

To import the data, the `Standalone Kakfa Connect` is used and the file and configurations
are kept inside the `docker` folder. To test locally with the data, you can
run `make connect-import` command. This will run the `kafka connect` inside the container
and will produce individual records in the file onto the topic `connect-input`.

To reimport data with a new file, use `make conect-reimport`. This will copy the latest data file
on local to the broker instance and start the standalone connect.

To get the complete set of make commands, use `make help`.

### Stream Processors ###
The stream configurations are defined in `application.yml` file. The configuration includes key parameters such as,
```$xslt
    broker binder       # The endpoint for broker configuration
    input binding       # Input stream configuration that includes, consumer topic name, KeySerde and ValueSerde
                            The KeySerde and ValueSerde are defined as String for the consumer
    output binding      # The remapped key values for the topic are defined here 
```

This stream listener class is defined in `com.abnamro.challenge.futuretxn.processor.TransactionProcessor`
supported by a binding class defined in `com.abnamro.challenge.futuretxn.processor.StreamBinding`.

The stream processor ingests records from the Kafka topic and generates materialized view for realitme
API calls and also propagates the reformatted messages to the other topics.

The materialized view include,
```
    customer_product_total      # This maintains a summary of customer/product key with a total of transaction amount.
                                    The view can be accessed through REST API query requests.
                                    Result of this materialzed view is sent to a Kafka topic `CUSTOMER_PRODUCT_TOTAL`
    
    CLIENT_COUNT                # This materialized view windows transactions fro a single customer and counts the number
                                    of transactions processed through the stream.
```  

## REST API Catalogue ##
The REST API for querying the Kafka streams is defined in `com.abnamro.challenge.futuretxn.api.Controller` class.
The controller allows a simple GET request for fetching the transaction amount summary for all customers and products.

## Scheduler ##
The report generation process is added as a Spring Scheduled task and the same will execute every day and generate the 
CSV file locally. 

`Scheduler - com.abnamro.challenge.futuretxn.scheduler.ScheduledTask` 

## Directory Structure ##

```bash
   pom.xml              # Maven project build Java API definition
   docker/              # Docker folder holding container definition
    docker-compose.yml  # Docker compose for local execution
    config/             # Configuration files for Kakfa connect
    data/               # Data files for testing input files on docker containers
   Makefile             # Orchastration scripts for provisioning and build
   src/
     main/
       java/            # Java source code
       resources/       # Application resources
        application.yml # Main configuration file, hodling kakfa parameters and stream config
        avro/           # Avro schema definitions
     test/
       java/            # Java test source code
       resources/       # Application test resources
``` 

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Make](https://ftp.gnu.org/old-gnu/Manuals/make-3.79.1/html_node/make_45.html)
* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.7.RELEASE/maven-plugin/)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.2.7.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
