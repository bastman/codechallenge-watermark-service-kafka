package com.bastman.codechallenge.watermarkservice.kafkarestservice.domain.service

import com.bastman.codechallenge.watermarkservice.domain.SourcePublication
import com.bastman.codechallenge.watermarkservice.domain.WatermarkedPublication
import com.bastman.codechallenge.watermarkservice.domain.kafka.KafkaEventSerializer
import com.bastman.codechallenge.watermarkservice.domain.kafka.KafkaPublicationReceivedEvent
import com.bastman.codechallenge.watermarkservice.kafkarestservice.configuration.kafka.KafkaSender
import com.bastman.codechallenge.watermarkservice.kafkarestservice.domain.repository.WatermarkedPublicationsRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

typealias WatermarkJobTicketIdSupplier = () -> String

@Component
class WatermarkService(
        private val kafkaSender: KafkaSender,
        private val watermarkedPublicationsRepository: WatermarkedPublicationsRepository,
        @Value("\${kafka.producerTopic}") val producerTopic: String
) {
    private val watermarkJobTicketIdSupplier: WatermarkJobTicketIdSupplier = {
        "${System.nanoTime().toString()}-${UUID.randomUUID().toString()}"
    }

    enum class JobStatus() {
        COMPLETE,
        NOT_FOUND
    }

    private val JSON = KafkaEventSerializer.JSON

    fun generateWatermarkJobTicketId() = watermarkJobTicketIdSupplier()

    fun submitJob(sourcePublication: SourcePublication): KafkaPublicationReceivedEvent {

        val event = KafkaPublicationReceivedEvent(
                eventTime = Instant.now(),
                ticketId = generateWatermarkJobTicketId(),
                sourcePublication = sourcePublication
        )

        val eventJson = JSON.encode(event)

        kafkaSender.send(topic = producerTopic, message = eventJson)

        return event
    }

    fun describeJobStatus(ticketId: String): JobStatus {
        val watermarkedPublication = getWatermarkedPublication(ticketId = ticketId)
        watermarkedPublication?.let { return JobStatus.COMPLETE }

        return JobStatus.NOT_FOUND
    }

    fun getWatermarkedPublication(ticketId: String): WatermarkedPublication? = watermarkedPublicationsRepository
            .getOrNull(itemId = ticketId)?.watermarkedPublication


}
