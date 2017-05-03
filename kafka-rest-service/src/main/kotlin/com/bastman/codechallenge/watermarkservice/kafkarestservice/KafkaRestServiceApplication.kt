package com.bastman.codechallenge.watermarkservice.kafkarestservice

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean

@SpringBootApplication
open class KafkaRestServiceApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplicationBuilder()
                    .sources(KafkaRestServiceApplication::class.java)
                    .web(true)
                    .run(*args)

            println("=== onApplicationIsReady =====")
        }
    }


    @Bean
    open fun init(ctx: ConfigurableApplicationContext) = CommandLineRunner {

    }
}