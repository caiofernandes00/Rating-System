package com.example.rating.adapter.kafka

import io.ktor.server.config.*

const val ratingsTopic = "ratings"
const val ratingsAverage = "ratings-average"

internal fun kafkaBootstrapServers(config: ApplicationConfig): List<String> {
    return config.propertyOrNull("ktor.kafka.bootstrap.servers")?.getList()
        ?: throw IllegalStateException("bootstrap servers are not set")
}