package com.example.rating.adapter.ktor.plugins

import com.example.rating.adapter.kafka.config.KafkaAdminConfig
import com.example.rating.adapter.kafka.ratingsAverageTopic
import com.example.rating.adapter.kafka.ratingsTopic
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.apache.kafka.clients.admin.NewTopic

fun Application.configureKafkaAdmin(kafkaConfig: ApplicationConfig) {
    install(KafkaAdminConfig) {
        applicationConfig = kafkaConfig
        topics = listOf(
            NewTopic(ratingsTopic, 1, 1),
            NewTopic(ratingsAverageTopic, 1, 1)
        )
    }
}