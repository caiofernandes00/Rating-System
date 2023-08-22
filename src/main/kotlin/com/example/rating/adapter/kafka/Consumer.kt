package com.example.rating.adapter.kafka

import com.example.rating.adapter.extensions.toMap
import io.ktor.server.config.*
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.ProducerConfig
import java.util.*

private const val KAFKA_DEFAULT_GROUP_ID = "ktor-consumer"

fun <K, V> createKafkaConsumer(
    config: ApplicationConfig,
    groupId: String = KAFKA_DEFAULT_GROUP_ID
): KafkaConsumer<K, V> {
    val consumerProperties = Properties().apply {
        putAll(config.toMap("ktor.kafka.properties"))
        putAll(config.toMap("ktor.kafka.consumer"))
        put("group.id", groupId)
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers(config))
    }

    return KafkaConsumer(consumerProperties)
}

fun <K, V> KafkaConsumer<K, V>.subscribe(): KafkaConsumer<K, V> {
    this.subscribe(listOf(ratingsAverageTopic))
    return this
}