package com.bastman.codechallenge.watermarkservice.domain.kafka

import com.bastman.codechallenge.watermarkservice.domain.SourcePublication
import com.bastman.codechallenge.watermarkservice.domain.WatermarkedPublication
import com.bastman.kjson.jackson.codec.Json
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import java.time.Instant

data class KafkaPublicationReceivedEvent(
        val eventTime: Instant,
        val ticketId: String,
        val sourcePublication: SourcePublication
)

data class KafkaWatermarkCompleteEvent(
        val eventTime: Instant,
        val ticketId: String,
        val watermarkedPublication: WatermarkedPublication
)

object KafkaEventSerializer {
    val JSON_BUILDER = Json.JsonBuilder
            .default()
            .withModules(
                    JavaTimeModule(),
                    Jdk8Module(),
                    ParameterNamesModule()
            )
            .withoutDeserializationFeature(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
            )
            .withoutSerializationFeature(
                    SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                    SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS
            )
    val JSON = JSON_BUILDER.build()
}
