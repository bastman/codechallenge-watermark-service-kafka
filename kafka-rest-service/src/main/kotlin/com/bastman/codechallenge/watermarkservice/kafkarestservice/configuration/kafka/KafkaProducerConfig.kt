package com.bastman.codechallenge.watermarkservice.kafkarestservice.configuration.kafka

import com.bastman.codechallenge.watermarkservice.logging.AppLogger
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import org.springframework.util.concurrent.ListenableFutureCallback
import java.util.*

// see: https://www.codenotfound.com/2016/09/spring-kafka-consumer-producer-example.html

@Component
open class KafkaProducerConfig(
        @Value("\${kafka.servers}") val kafkaServers: String,
        @Value("\${kafka.appId}") val appId: String,
        @Value("\${kafka.producerTopic}") val producerTopic: String
) {
    @Bean
    fun producerConfigs(): Map<String, Any> {
        val props = HashMap<String, Any>()
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers)
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)

        return props
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        return DefaultKafkaProducerFactory<String, String>(producerConfigs())
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate<String, String>(producerFactory())
    }

    @Bean
    fun sender(): KafkaSender {
        return KafkaSender()
    }
}

class KafkaSender {

    @Autowired
    private val kafkaTemplate: KafkaTemplate<String, String>? = null

    fun send(topic: String, message: String) {
        // the KafkaTemplate provides asynchronous send methods returning a Future
        val future = kafkaTemplate!!.send(topic, message)

        // register a callback with the listener to receive the result of the send asynchronously
        future.addCallback(object : ListenableFutureCallback<SendResult<String, String>> {

            override fun onSuccess(result: SendResult<String, String>) {
                LOGGER.info("sent to topic=$topic message='{}' with offset={}", message,
                        result.getRecordMetadata().offset())
            }

            override fun onFailure(ex: Throwable) {
                LOGGER.error("unable to topic=$topic send message='{}'", message, ex)
            }
        })

        // or, to block the sending thread to await the result, invoke the future's get() method
    }

    companion object {

        private val LOGGER = AppLogger.get(KafkaSender::class.java)
    }
}