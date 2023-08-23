package com.example.rating.adapter.kafka

import com.example.rating.adapter.extensions.toMap
import com.example.rating.domain.Rating
import io.confluent.kafka.streams.serdes.json.KafkaJsonSchemaSerde
import io.ktor.server.config.*
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.Serdes.Long
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import java.util.*

fun processAverageRating(config: ApplicationConfig) {
    val properties = kafkaStreamProperties(config)
    val streamsBuilder = StreamsBuilder()
    val topology = buildTopology(streamsBuilder, properties)
}

private fun kafkaStreamProperties(config: ApplicationConfig): Properties {
    return Properties().apply {
        putAll(config.toMap("ktor.kafka.properties"))
        putAll(config.toMap("ktor.kafka.streams"))
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers(config))
    }
}

private fun buildTopology(
    streamsBuilder: StreamsBuilder,
    properties: Properties
): Topology {
    val ratingStream: KStream<Long, Rating> = ratingStream(streamsBuilder, properties)

    return Topology()
}

private fun ratingStream(
    streamsBuilder: StreamsBuilder,
    properties: Properties
): KStream<Long, Rating> {
    return streamsBuilder.stream(
        ratingsTopic,
        Consumed.with(Long(), jsonSchemaSerde(properties, true))
    )
}

private inline fun <reified V> jsonSchemaSerde(
    properties: Properties,
    isKeySerde: Boolean
): KafkaJsonSchemaSerde<V> {
    val schemaSerde = KafkaJsonSchemaSerde(V::class.java)

    return schemaSerde
}