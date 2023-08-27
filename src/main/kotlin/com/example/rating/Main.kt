package com.example.rating

import com.example.rating.adapter.kafka.createKafkaConsumer
import com.example.rating.adapter.kafka.createKafkaProducer
import com.example.rating.adapter.kafka.processAverageRating
import com.example.rating.adapter.kafka.subscribe
import com.example.rating.adapter.ktor.plugin.configureDefaultHeaders
import com.example.rating.adapter.ktor.plugin.configureHttp
import com.example.rating.adapter.ktor.plugin.configureKafkaAdmin
import com.example.rating.adapter.ktor.plugin.configureWebsockets
import com.example.rating.domain.Rating
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.netty.*
import java.time.Duration

val currentEnv: String = System.getProperty("io.ktor.env") ?: "local"
val kafkaConfig = ApplicationConfig("kafka.$currentEnv.conf")

fun main(args: Array<String>): Unit = EngineMain.main(args)


fun Application.kafkaModule(testing: Boolean = false) {
    configureKafkaAdmin(kafkaConfig)
    val streams = processAverageRating(kafkaConfig)

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

    configureDefaultHeaders()
    configureWebsockets(consumer)
    configureHttp(producer)
}

