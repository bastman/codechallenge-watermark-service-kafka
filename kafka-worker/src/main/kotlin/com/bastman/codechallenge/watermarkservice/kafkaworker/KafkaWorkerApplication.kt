package com.bastman.codechallenge.watermarkservice.kafkaworker

import com.bastman.codechallenge.watermarkservice.kafkaworker.pipeline.KafkaWorker
import com.bastman.kdefer.Defer
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import javax.annotation.PreDestroy

@SpringBootApplication
open class KafkaWorkerApplication {

    private val defer = Defer()

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplicationBuilder()
                    .sources(KafkaWorkerApplication::class.java)
                    .web(false)
                    .run(*args)

            println("=== onApplicationIsReady =====")
        }
    }

    @PreDestroy
    private fun close() {
        defer.close()
    }

    @Bean
    open fun init(ctx: ConfigurableApplicationContext, kafkaWorker: KafkaWorker) = CommandLineRunner {

        val kafkaStreams = kafkaWorker.createStreams()
        defer.addGraceful { kafkaStreams.close() }

        kafkaStreams.start()
    }
}