package com.example.rating.adapter.kafka

import com.example.rating.adapter.extensions.toMap
import com.example.rating.adapter.repository.RatingsAverageRepository
import com.example.rating.domain.CountAndSum
import com.example.rating.domain.Rating
import com.example.rating.domain.RatingAverage
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.*
import io.confluent.kafka.streams.serdes.json.KafkaJsonSchemaSerde
import io.ktor.server.config.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.Serdes.*
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.*
import org.apache.kafka.streams.kstream.Grouped.with
import org.apache.kafka.streams.state.KeyValueStore
import java.util.*

class Stream(
    private val config: ApplicationConfig,
    private val ratingsAverageRepository: RatingsAverageRepository
) {
    suspend fun processAverageRating(): KafkaStreams {
        val properties = kafkaStreamProperties(config)
        val streamsBuilder = StreamsBuilder()
        val topology = buildTopology(streamsBuilder, properties)

        return KafkaStreams(topology, properties)
    }

    private fun kafkaStreamProperties(config: ApplicationConfig): Properties {
        return Properties().apply {
            putAll(config.toMap("ktor.kafka.properties"))
            putAll(config.toMap("ktor.kafka.streams"))
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers(config))
        }
    }

    private suspend fun buildTopology(
        streamsBuilder: StreamsBuilder,
        properties: Properties
    ): Topology {
        val ratingStream: KStream<Long, Rating> = ratingStream(streamsBuilder, properties)

        getRatingAverageTable(
            ratingStream,
            ratingsAverageTopic,
            jsonSchemaSerde(properties, false)
        )

        return streamsBuilder.build()
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

    private suspend fun getRatingAverageTable(
        ratingStream: KStream<Long, Rating>,
        avgRatingTopicName: String,
        countAndSumSerde: KafkaJsonSchemaSerde<CountAndSum>
    ): KTable<Long, Double> {
        val ratingsById: KGroupedStream<Long, Double> = ratingStream
            .map { _, rating -> KeyValue(rating.movieId, rating.rating) }
            .groupByKey(with(Long(), Double()))

        val ratingCountAndSum: KTable<Long, CountAndSum> = ratingsById.aggregate(
            { CountAndSum() },
            { _, rating, countAndSum ->
                countAndSum.count++
                countAndSum.sum += rating
                countAndSum
            },
            Materialized.with(Long(), countAndSumSerde)
        )

        val ratingAverage: KTable<Long, Double> = ratingCountAndSum.mapValues(
            { countAndSum -> countAndSum.sum / countAndSum.count },
            Materialized.`as`<Long, Double, KeyValueStore<Bytes, ByteArray>>(ratingsAverageTopic)
                .withKeySerde(LongSerde())
                .withValueSerde(DoubleSerde())
        )

        val stream = ratingAverage.toStream()
        stream.to(avgRatingTopicName, producedWith<Long, Double>())
        saveToDatabase(stream)

        return ratingAverage
    }

    private suspend fun saveToDatabase(stream: KStream<Long, Double>) {
        val coroutineScope = CoroutineScope(Dispatchers.Default)

        stream.foreach { key, average ->
            coroutineScope.launch {
                ratingsAverageRepository.create(RatingAverage(key, average))
            }
        }
    }

    private inline fun <reified V> jsonSchemaSerde(
        properties: Properties,
        isKeySerde: Boolean
    ): KafkaJsonSchemaSerde<V> {
        val schemaSerde = KafkaJsonSchemaSerde(V::class.java)
        val crSource = properties[BASIC_AUTH_CREDENTIALS_SOURCE]
        val uiConfig = properties[USER_INFO_CONFIG]

        val map = mutableMapOf(
            SCHEMA_REGISTRY_URL_CONFIG to properties[SCHEMA_REGISTRY_URL_CONFIG],
        )
        crSource?.let {
            map[BASIC_AUTH_CREDENTIALS_SOURCE] = crSource
        }
        uiConfig?.let {
            map[USER_INFO_CONFIG] = uiConfig
        }

        schemaSerde.configure(map, isKeySerde)
        return schemaSerde
    }

    private inline fun <reified K, reified V> producedWith(): Produced<K, V> =
        Produced.with(serdeFrom(K::class.java), serdeFrom(V::class.java))
}


