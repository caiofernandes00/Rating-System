package com.example.rating.adapter.ktor.plugins

import com.example.rating.adapter.extensions.logger
import com.example.rating.domain.Rating
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration

fun Application.configureWebsockets(
    kafkaConsumer: KafkaConsumer<Long, Double>
) {
    val log = logger<Application>()

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(10)
        timeout = Duration.ofSeconds(10)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/kafka") {
            val clientId = call.parameters["clientId"] ?: "unknown"
            log.debug("Client $clientId connected")

            try {
                while (true) {
                    poll(kafkaConsumer)
                }
            } finally {
                kafkaConsumer.apply {
                    unsubscribe()
                }

                log.info("Consumer for client ${kafkaConsumer.groupMetadata().groupId()} unsubscribed and closed...")
            }
        }
    }
}

private suspend fun DefaultWebSocketServerSession.poll(kafkaConsumer: KafkaConsumer<Long, Double>) =
    withContext(Dispatchers.IO) {
        kafkaConsumer.poll(Duration.ofMillis(100))
            .forEach {
                outgoing.send(
                    Frame.Text(
                        Rating(it.key(), it.value()).toString()
                    )
                )
            }

    }