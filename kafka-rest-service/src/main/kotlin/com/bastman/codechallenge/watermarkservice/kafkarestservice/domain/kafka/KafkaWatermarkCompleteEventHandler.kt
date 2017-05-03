package com.bastman.codechallenge.watermarkservice.kafkarestservice.domain.kafka

import com.bastman.codechallenge.watermarkservice.domain.kafka.KafkaEventSerializer
import com.bastman.codechallenge.watermarkservice.domain.kafka.KafkaWatermarkCompleteEvent
import com.bastman.codechallenge.watermarkservice.kafkarestservice.domain.repository.WatermarkedPublicationsRepository
import com.bastman.codechallenge.watermarkservice.logging.AppLogger
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicLong

@Component
class KafkaWatermarkCompleteEventHandler(
        val watermarkedPublicationsRepository: WatermarkedPublicationsRepository
) {

    val LOGGER = AppLogger.get(this::class.java)

    private val JSON = KafkaEventSerializer.JSON

    private val itemsProcessedCounter: AtomicLong = AtomicLong(0)
    private val itemsAcceptedCounter: AtomicLong = AtomicLong(0)


    fun getItemsProcessedCount(): Long {
        return itemsProcessedCounter.get()
    }

    fun getItemsAcceptedCount(): Long {
        return itemsAcceptedCounter.get()
    }

    fun handle(json: String) {

        itemsProcessedCounter.incrementAndGet()
        LOGGER.debug("LISTEN: json=$json")

        val event: KafkaWatermarkCompleteEvent? = try {
            JSON.decode<KafkaWatermarkCompleteEvent>(json)
        } catch (all: Throwable) {
            null
        }

        if (event == null) {
            logEventRejected("failed to parse event: record=$json")

            return
        }

        val isAccepted = acceptSourceEvent(event = event)

        if (!isAccepted) {

            return
        }

        itemsAcceptedCounter.incrementAndGet()

        watermarkedPublicationsRepository.add(event)

        LOGGER.info(
                "accept event. added to repository. ticketId=${event.ticketId}"
        )

    }

    private fun acceptSourceEvent(event: KafkaWatermarkCompleteEvent): Boolean {
        // implement
        return true
    }

    private fun logEventRejected(message: String) {
        LOGGER.debug(message)
    }
}