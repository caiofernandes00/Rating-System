package com.example.rating.adapter.kafka

import com.example.rating.adapter.extensions.toMap
import io.ktor.server.config.*
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import java.util.*
import java.util.concurrent.Future


fun <K, V> createKafkaProducer(config: ApplicationConfig): KafkaProducer<K, V> {
    val producerProperties = Properties().apply {
        putAll(config.toMap("ktor.kafka.properties"))
        putAll(config.toMap("ktor.kafka.producer"))
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers(config))
    }

    return KafkaProducer(producerProperties)
}

fun <K, V> KafkaProducer<K, V>.send(topic: String, key: K, value: V): Future<RecordMetadata> {
    return this.send(ProducerRecord(topic, key, value))
}