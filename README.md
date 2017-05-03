# codechallenge-watermark-service-kafka

## status

- currently implementing a proof-of-concept
- this is not ready yet.

## TODO
- make all components work together
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
        