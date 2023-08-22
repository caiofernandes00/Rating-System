package com.example.rating.adapter.ktor.plugin

import com.example.rating.adapter.extensions.logger
import com.example.rating.adapter.kafka.send
import com.example.rating.adapter.ktor.Html.Html
import com.example.rating.domain.Rating
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import java.time.Duration

@Serializable
data class Status(val message: String)

fun Application.configureRouting(
    kafkaProducer: KafkaProducer<Long, Rating>,
    kafkaConsumer: KafkaConsumer<Long, Double>
) {
    val log = logger<Application>()

    install(ContentNegotiation) {
        json()
    }
    routing {
        get("/") {
            call.respondHtml(
                HttpStatusCode.OK,
                Html.indexHTML
            )
        }

        post("/rating") {
            val rating = call.receive<Rating>()

            kafkaProducer.send("ratings", rating.movieId, rating)
            call.respond(HttpStatusCode.Accepted, Status("Rating accepted"))
        }

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