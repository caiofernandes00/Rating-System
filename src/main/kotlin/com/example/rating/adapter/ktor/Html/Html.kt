package com.example.rating.adapter.ktor.Html

import kotlinx.html.FlowContent
import kotlinx.html.*

object Html {

    private fun page(content: FlowContent.() -> Unit = {}): HTML.() -> Unit = {
        head {
            title("Ktor Kafka App")
        }

        body {
            div("container rounded") {
                content()
            }
        }
    }

    val indexHTML = page() {
        h1 {
            +"Hello from Ktor Kafka App"
        }
    }
}