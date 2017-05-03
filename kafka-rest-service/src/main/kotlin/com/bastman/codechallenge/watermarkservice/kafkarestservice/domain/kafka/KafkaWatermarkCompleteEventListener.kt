package com.bastman.codechallenge.watermarkservice.kafkarestservice.domain.kafka

import com.bastman.codechallenge.watermarkservice.kafkarestservice.configuration.SpringConstants
import com.bastman.codechallenge.watermarkservice.logging.AppLogger
import kotlinx.coroutines.experimental.future.future
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener


class KafkaWatermarkCompleteEventListener {

    @Autowired lateinit var eventHandler: KafkaWatermarkCompleteEventHandler

    val LOGGER = AppLogger.get(this::class.java)

    @KafkaListener(
            id = SpringConstants.KAFKA_CONSUMER_LISTENER_ID,
            containerFactory = SpringConstants.KAFKA_CONSUMER_CONTAINER_FACTORY,
            topics = arrayOf("\${kafka.consumerTopic}")
    )
    fun listen(recordValues: List<String>) {
        future {

            recordValues.forEach {
                eventHandler.handle(json = it)
            }

            LOGGER.info(listOf<String>(
                    "======== SOURCE EVENTS:",
                    "${eventHandler.getItemsProcessedCount()} processed",
                    "/ ${eventHandler.getItemsAcceptedCount()} accepted",
                    "======="

            ).joinToString(" "))
        }
    }
}

