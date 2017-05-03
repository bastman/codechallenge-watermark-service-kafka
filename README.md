# codechallenge-watermark-service-kafka

- based on : https://github.com/bastman/codechallenge-watermark-service
- but uses kafka as persistent storage engine

## status

- currently implementing a proof-of-concept. this is not ready yet.
- all components are working together

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
        