package com.example.rating.adapter.kafka

import io.ktor.server.config.*
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.ProducerConfig
import java.util.*

private const val KAFKA_DEFAULT_GROUP_ID = "ktor-consumer"

private fun <K, V> buildConsumer(
    config: ApplicationConfig,
    groupId: String = KAFKA_DEFAULT_GROUP_ID
): KafkaConsumer<K, V> {
    val consumerProperties = Properties().apply {
        putAll(kafkaCommonConfig(config))
        putAll(kafkaConsumerConfig(config))
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers(config))
    }

    return KafkaConsumer(consumerProperties)
}

fun <K, V> createKafkaConsumer(
    config: ApplicationConfig,
    topic: String,
    groupId: String = KAFKA_DEFAULT_GROUP_ID
): KafkaConsumer<K, V> {
    val consumer = buildConsumer<K, V>(config, groupId)
    consumer.subscribe(listOf(topic))
    return consumer
}