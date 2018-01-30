# codechallenge-watermark-service-kafka

- based on : https://github.com/bastman/codechallenge-watermark-service
- but uses kafka as persistent storage engine

## the pipeline - CQRS & EventSourcing
- rest-api (as producer): create a job -> store job into kafka-topic
- kafka-worker: process jobs from kafka-source-topic and put result into kafka-sink-topic
- rest-api (as consumer): listen to kafka-sink-topic and keep results in-memory
- rest-api: provide interface to access jobs being processed.

## status

- proof-of-concept.

## TODO
- try to get rid of jackson's type hints, which pollute the data model
- cleanup code base
- provide tests
## components

- kafka (docker-image: spotify/kafka)
- kafka-worker (spring-boot, kafka-streams)
- kafka-rest-api (spring-boot, spring-kafka)

## run kafka

    $ docker-compose up
    
## run kafka-worker
    $ ./gradlew :kafka-worker:bootRun
    
## run kafka-rest-api
    $ ./gradlew :kafka-rest-service:bootRun
    
## Troubleshooting
    - the default installation assumes kafka running at 127.0.0.1:9092
    - if you run kafka on a different host/port, you may need to adjust the kafka-server settings defined application.yml of each gradle module
        
