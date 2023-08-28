package com.example.rating

import com.example.rating.adapter.kafka.*
import com.example.rating.adapter.ktor.plugins.configureDefaultHeaders
import com.example.rating.adapter.ktor.plugins.configureHttp
import com.example.rating.adapter.ktor.plugins.configureKafkaAdmin
import com.example.rating.adapter.ktor.plugins.configureWebsockets
import com.example.rating.adapter.repository.RatingsAverageRepository
import com.example.rating.adapter.repository.RatingsRepository
import com.example.rating.adapter.repository.config.configureDatabase
import com.example.rating.domain.Rating
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.netty.*
import java.time.Duration

val currentEnv: String = System.getenv("KTOR_ENV") ?: "local"
val kafkaConfig = ApplicationConfig("kafka/kafka.$currentEnv.conf")
val databaseConfig = ApplicationConfig("database/database.$currentEnv.conf")
val dbConnect = configureDatabase(databaseConfig)

fun main(args: Array<String>): Unit = EngineMain.main(args)


fun Application.kafkaModule(testing: Boolean = false) {
    configureKafkaAdmin(kafkaConfig)
    val ratingsAverageRepository = RatingsAverageRepository(dbConnect)

    val streams = Stream(kafkaConfig, ratingsAverageRepository).processAverageRating()

    environment.monitor.subscribe(ApplicationStarted) {
        streams.cleanUp()
        streams.start()
        log.info("Kafka Streams started")
    }

    environment.monitor.subscribe(ApplicationStopped) {
        log.info("Kafka Streams stopped")
        streams.close(Duration.ofSeconds(5))
    }
}

fun Application.producerConsumerModule(testing: Boolean = false) {
    val producer = createKafkaProducer<Long, Rating>(kafkaConfig)
    val consumer = createKafkaConsumer<Long, Double>(kafkaConfig).run { subscribe() }
    val ratingsRepository = RatingsRepository(dbConnect)

    configureDefaultHeaders()
    configureWebsockets(consumer)
    configureHttp(producer, ratingsRepository)
}

fun Application.databaseModule(testing: Boolean = false) {
    configureDatabase(databaseConfig)
}

