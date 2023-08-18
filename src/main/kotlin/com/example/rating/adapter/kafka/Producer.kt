package com.example.rating.adapter.kafka

import io.ktor.server.config.*
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*

fun <K, V> buildProducer(config: ApplicationConfig): KafkaProducer<K, V> {
    val bootstrapServers = config.propertyOrNull("ktor.kafka.bootstrap.servers")?.getList()
        ?: throw IllegalStateException("bootstrap servers are not set")

    val producerProperties = Properties().apply {
        putAll(kafkaCommonConfig(config))
        putAll(kafkaProducerConfig(config))
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    }


    val kakfaPropertiesDefault = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
        put(ProducerConfig.ACKS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
        put("schema.registry.url", "http://localhost:8081")
    }

    return KafkaProducer(producerProperties)
}

fun <K, V> KafkaProducer<K, V>.send(topic: String, key: K, value: V) {
    this.send(ProducerRecord(topic, key, value))
}

private fun kafkaCommonConfig(config: ApplicationConfig): Map<*, *> {
    val ktor = config.toMap()["ktor"] as Map<*, *>
    val kafka = ktor["kafka"] as Map<*, *>
    val commonConfig = kafka["properties"] as Map<*, *>
    return commonConfig
}

private fun kafkaProducerConfig(config: ApplicationConfig): Map<*, *> {
    val ktor = config.toMap()["ktor"] as Map<*, *>
    val kafka = ktor["kafka"] as Map<*, *>
    val producerConfig = kafka["producer"] as Map<*, *>
    return producerConfig
}