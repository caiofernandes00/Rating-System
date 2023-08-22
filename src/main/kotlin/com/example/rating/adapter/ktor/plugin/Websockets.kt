package com.example.rating.adapter.ktor.plugin

import io.ktor.server.application.*
import io.ktor.server.websocket.*
import java.time.Duration

fun Application.configureWebsockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(10)
        timeout = Duration.ofSeconds(10)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
}