package com.example.rating.adapter.kafka

import com.example.rating.adapter.kafka.admin.KafkaConfig
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.streams.StreamsBuilder

fun Application.configureKafkaStream(kafkaConfig: ApplicationConfig) {
    install(KafkaConfig) {
        applicationConfig = kafkaConfig
        topics = listOf(
            NewTopic(ratingsTopic, 1, 1),
            NewTopic(ratingsAverageTopic, 1, 1)
        )
    }

    val streamsBuilder = StreamsBuilder()


}