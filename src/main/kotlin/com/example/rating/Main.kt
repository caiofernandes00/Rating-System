package com.example.rating

import com.example.rating.adapter.kafka.createKafkaConsumer
import com.example.rating.adapter.kafka.createKafkaProducer
import com.example.rating.adapter.kafka.subscribe
import com.example.rating.adapter.ktor.plugin.configureDefaultHeaders
import com.example.rating.adapter.ktor.plugin.configureRouting
import com.example.rating.adapter.ktor.plugin.configureWebsockets
import com.example.rating.domain.Rating
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    val config = ApplicationConfig("kafka.conf")
    val producer = createKafkaProducer<Long, Rating>(config)
    val consumer = createKafkaConsumer<Long, Double>(config).run { subscribe() }

    configureDefaultHeaders()
    configureWebsockets()
    configureRouting(producer, consumer)
}
