package com.example.rating

import com.example.rating.adapter.ktor.plugin.configureKafkaAdmin
import com.example.rating.adapter.kafka.createKafkaConsumer
import com.example.rating.adapter.kafka.createKafkaProducer
import com.example.rating.adapter.kafka.processAverageRating
import com.example.rating.adapter.kafka.subscribe
import com.example.rating.adapter.ktor.plugin.configureDefaultHeaders
import com.example.rating.adapter.ktor.plugin.configureHttp
import com.example.rating.adapter.ktor.plugin.configureWebsockets
import com.example.rating.domain.Rating
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.netty.*

val kafkaConfig = ApplicationConfig("kafka.conf")

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.producerConsumerModule(testing: Boolean = false) {
    val producer = createKafkaProducer<Long, Rating>(kafkaConfig)
    val consumer = createKafkaConsumer<Long, Double>(kafkaConfig).run { subscribe() }

    configureDefaultHeaders()
    configureWebsockets(consumer)
    configureHttp(producer)
}

fun Application.streamModule(testing: Boolean = false) {
    configureKafkaAdmin(kafkaConfig)
    processAverageRating(kafkaConfig)
}