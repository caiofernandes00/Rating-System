package com.example.rating.adapter.ktor.plugin

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
import kotlinx.serialization.Serializable
import org.apache.kafka.clients.producer.KafkaProducer

@Serializable
data class Status(val message: String)

fun Application.configureHttp(
    kafkaProducer: KafkaProducer<Long, Rating>
) {
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
    }
}
