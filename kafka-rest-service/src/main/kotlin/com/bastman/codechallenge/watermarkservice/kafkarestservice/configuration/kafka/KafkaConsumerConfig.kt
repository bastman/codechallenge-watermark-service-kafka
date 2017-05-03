package com.bastman.codechallenge.watermarkservice.kafkarestservice.configuration.kafka

import com.bastman.codechallenge.watermarkservice.kafkarestservice.configuration.SpringConstants
import com.bastman.codechallenge.watermarkservice.kafkarestservice.domain.kafka.KafkaWatermarkCompleteEventListener
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
@EnableKafka
open class KafkaConsumerConfig(
        @Value("\${kafka.servers}") val kafkaServers: String,
        @Value("\${kafka.consumerGroupIdPrefix}") val consumerGroupIdPrefix: String,
        @Value("\${kafka.consumerTopic}") val consumerTopic: String
) {

    // see: https://github.com/spring-projects/spring-kafka/issues/132
    // see: http://docs.spring.io/spring-kafka/docs/1.0.0.RC1/reference/htmlsingle/
    @Bean(name = arrayOf(SpringConstants.KAFKA_CONSUMER_CONTAINER_FACTORY))
    open fun kafkaListenerContainerFactory()
            : KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> {
        val containerFactory = ConcurrentKafkaListenerContainerFactory<String, String>()

        val consumerFactory: ConsumerFactory<String, String> = DefaultKafkaConsumerFactory(
                consumerConfigs()
        )

        containerFactory.consumerFactory = consumerFactory
        containerFactory.setConcurrency(1)
        containerFactory.containerProperties.pollTimeout = 3000
        containerFactory.isBatchListener = true

        return containerFactory
    }

    fun consumerConfigs(): Map<String, Any> {

        val nowInMs = Instant.now().toEpochMilli()
        val randomInt = Random().nextInt()
        val prefix = consumerGroupIdPrefix.trim()
        val groupId = "$prefix-$nowInMs-$randomInt"

        val propsMap: MutableMap<String, Any> = mutableMapOf()
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers)
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
        propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100")
        propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000")
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        propsMap.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5000)
        propsMap.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 20 * 1024 * 1024)

        return propsMap.toMap()
    }

    @Bean
    open fun listener(): KafkaWatermarkCompleteEventListener {
        return KafkaWatermarkCompleteEventListener()
    }

}