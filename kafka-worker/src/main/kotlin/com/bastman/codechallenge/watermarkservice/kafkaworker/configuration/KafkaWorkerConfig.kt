package com.bastman.codechallenge.watermarkservice.kafkaworker.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
open class KafkaWorkerConfig(
        @Value("\${kafka.servers}") val kafkaServers: String,
        @Value("\${kafka.appId}") val appId: String,
        @Value("\${kafka.sourceTopic}") val sourceTopic: String,
        @Value("\${kafka.sinkTopic}") val sinkTopic: String,
        @Value("\${kafka.sendToSinkEnabled}") val sendToSinkEnabled: Boolean
) {}