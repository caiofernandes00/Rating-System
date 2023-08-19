package com.example.rating.adapter.kafka

import io.ktor.server.config.*

const val ratingsTopic = "ratings"
const val ratingsAverage = "ratings-average"

internal fun kafkaBootstrapServers(config: ApplicationConfig): List<String> {
    return config.propertyOrNull("ktor.kafka.bootstrap.servers")?.getList()
        ?: throw IllegalStateException("bootstrap servers are not set")
}

internal fun kafkaCommonConfig(config: ApplicationConfig): Map<*, *> {
    val ktor = config.toMap()["ktor"] as Map<*, *>
    val kafka = ktor["kafka"] as Map<*, *>
    return kafka["properties"] as Map<*, *>
}

internal fun kafkaProducerConfig(config: ApplicationConfig): Map<*, *> {
    val ktor = config.toMap()["ktor"] as Map<*, *>
    val kafka = ktor["kafka"] as Map<*, *>
    return kafka["producer"] as Map<*, *>
}

internal fun kafkaConsumerConfig(config: ApplicationConfig): Map<*, *> {
    val ktor = config.toMap()["ktor"] as Map<*, *>
    val kafka = ktor["kafka"] as Map<*, *>
    return kafka["consumer"] as Map<*, *>
}