package com.example.rating.adapter.ktor.plugin

import com.example.rating.adapter.ktor.Html.Html
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*

fun Application.configureRouting(testing: Boolean = false) {
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
    }
}