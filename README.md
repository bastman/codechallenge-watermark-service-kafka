# codechallenge-watermark-service-kafka

- based on : https://github.com/bastman/codechallenge-watermark-service
- but uses kafka as persistent storage engine

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
        
