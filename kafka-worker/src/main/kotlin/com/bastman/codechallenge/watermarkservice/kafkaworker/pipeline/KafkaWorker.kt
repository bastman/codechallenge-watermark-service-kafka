package com.bastman.codechallenge.watermarkservice.kafkaworker.pipeline

import com.bastman.codechallenge.watermarkservice.configuration.AppConfigurationException
import com.bastman.codechallenge.watermarkservice.domain.kafka.KafkaEventSerializer
import com.bastman.codechallenge.watermarkservice.domain.kafka.KafkaPublicationReceivedEvent
import com.bastman.codechallenge.watermarkservice.domain.kafka.KafkaWatermarkCompleteEvent
import com.bastman.codechallenge.watermarkservice.domain.watermarkPublication
import com.bastman.codechallenge.watermarkservice.kafkaworker.configuration.KafkaWorkerConfig
import com.bastman.codechallenge.watermarkservice.logging.AppLogger
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.apache.kafka.streams.processor.WallclockTimestampExtractor
import org.springframework.stereotype.Component
import java.time.Instant
import javax.annotation.PostConstruct

@Component
class KafkaWorker(workerWorkerConfig: KafkaWorkerConfig) {

    private val LOGGER = AppLogger.get(this::class.java)

    private val JSON = KafkaEventSerializer.JSON

    private val kafkaProps = mapOf<String, Any>(
            StreamsConfig.APPLICATION_ID_CONFIG to workerWorkerConfig.appId,
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to workerWorkerConfig.kafkaServers,
            StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG to WallclockTimestampExtractor::class.java,
            StreamsConfig.REPLICATION_FACTOR_CONFIG to 1
            // , StreamsConfig.RETRY_BACKOFF_MS_CONFIG to Duration.ofSeconds(10).toMillis()
            //, ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest"
    )

    private val sourceTopic = workerWorkerConfig.sourceTopic
    private val sinkTopic = workerWorkerConfig.sinkTopic
    private val sendToSinkEnabled = workerWorkerConfig.sendToSinkEnabled

    @PostConstruct
    private fun validate() {
        if (sourceTopic == sinkTopic) {

            throw AppConfigurationException("Config Error! sourceTopic must not equal sinkTopic!")
        }
    }

    fun createStreams(): KafkaStreams {

        val streamConfig = StreamsConfig(kafkaProps)

        val stringSerializer = StringSerializer()
        val stringDeserializer = StringDeserializer()
        val stringSerde = Serdes.serdeFrom(stringSerializer, stringDeserializer)

        val builder = KStreamBuilder()

        val feeds = builder.stream<String, String>(stringSerde, stringSerde, sourceTopic)

        feeds.flatMapValues({
            sourceValueJson: String ->
            watermark(sourceValueJson)
        }).mapValues({
            sinkEvent: KafkaWatermarkCompleteEvent ->
            JSON.encode(sinkEvent)
        }).filter { _, value ->
            val keep = (value != null && sendToSinkEnabled)

            if (keep) {
                LOGGER.info("send to kafka: event=$value")
            } else {
                LOGGER.info("reject: event=$value")
            }

            keep
        }
                .to(stringSerde, stringSerde, sinkTopic)

        val streams = KafkaStreams(builder, streamConfig)

        return streams
    }

    private fun watermark(sourceValueJson: String): List<KafkaWatermarkCompleteEvent> {
        LOGGER.info("GOT SOMETHING !!!! $sourceValueJson")

        val sourceEvent: KafkaPublicationReceivedEvent = try {
            JSON.decode<KafkaPublicationReceivedEvent>(sourceValueJson)
        } catch (all: Throwable) {
            LOGGER.info("FAILED TO json decode !!!! $sourceValueJson")
            return emptyList()
        }

        LOGGER.info("decoded. $sourceValueJson")
        val watermarkedPublication = watermarkPublication(sourceEvent.sourcePublication)

        val sinkEvent = KafkaWatermarkCompleteEvent(
                eventTime = Instant.now(),
                ticketId = sourceEvent.ticketId,
                watermarkedPublication = watermarkedPublication
        )

        return listOf(sinkEvent)
    }


}