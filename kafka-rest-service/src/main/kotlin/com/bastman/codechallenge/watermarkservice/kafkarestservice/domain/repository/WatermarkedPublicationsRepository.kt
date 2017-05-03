package com.bastman.codechallenge.watermarkservice.kafkarestservice.domain.repository

import com.bastman.codechallenge.watermarkservice.logging.AppLogger
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.TimeUnit


import com.bastman.codechallenge.watermarkservice.domain.kafka.KafkaWatermarkCompleteEvent as RepositoryItem

typealias RepositoryCache = Cache<String, RepositoryItem>

@Component
class WatermarkedPublicationsRepository {
    private val LOGGER = AppLogger.get(javaClass)

    private val cache: RepositoryCache by lazy {
        val expiry = Duration.ofDays(3)

        Caffeine
                .newBuilder()
                .maximumSize(1_000_000)
                .expireAfterWrite(expiry.seconds, TimeUnit.SECONDS)
                .build<String, RepositoryItem>()
    }

    fun add(item: RepositoryItem) {
        val itemId = supplyItemId(item)
        cache.put(itemId, item)

        LOGGER.info("add item to repository. itemId=$itemId")
    }

    fun getOrNull(itemId: String) = cache.getIfPresent(itemId)
}

fun WatermarkedPublicationsRepository.supplyItemId(item: RepositoryItem) = item.ticketId