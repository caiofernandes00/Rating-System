package com.example.rating

import com.example.rating.adapter.kafka.buildProducer
import com.example.rating.adapter.ktor.plugin.configureDefaultHeaders
import com.example.rating.adapter.ktor.plugin.configureRouting
import com.example.rating.domain.Rating
import io.confluent.developer.extension.logger
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    val log = logger<Application>()

    configureDefaultHeaders()
    configureRouting()

    val config = ApplicationConfig("kafka.conf")
    buildProducer<Long, Rating>(config)
}
