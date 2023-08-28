package com.example.rating.adapter.kafka.config

import com.example.rating.adapter.extensions.logger
import com.example.rating.adapter.extensions.toMap
import com.example.rating.adapter.kafka.kafkaBootstrapServers
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.util.*
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.CreateTopicsResult
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import java.util.*

class KafkaAdminConfig(configuration: Configuration) {
    private val log = logger<KafkaAdminConfig>()
    private val applicationConfig = configuration.applicationConfig
    private val topics = configuration.topics

    data class Configuration(
        var applicationConfig: ApplicationConfig = ApplicationConfig("kafka/kafka.local.docker.conf"),
        var topics: List<NewTopic> = emptyList()
    )

    private fun createTopics() {
        val properties: Properties = getProperties(applicationConfig)

        val adminClient = AdminClient.create(properties)
        val createTopicsResult: CreateTopicsResult = adminClient.createTopics(topics)

        createTopicsResult.values().forEach { (k, _) ->
            log.debug("Topic $k created...")
        }
    }

    private fun getProperties(config: ApplicationConfig): Properties =
        Properties().apply {
            putAll(config.toMap("ktor.kafka.properties"))
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers(config))
        }

    companion object Plugin : BaseApplicationPlugin<Application, Configuration, KafkaAdminConfig> {
        override val key: AttributeKey<KafkaAdminConfig>
            get() = AttributeKey("kafka")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): KafkaAdminConfig {
            val configuration = Configuration().apply(configure)
            val kafkaFeature = KafkaAdminConfig(configuration)

            kafkaFeature.createTopics()
            return kafkaFeature
        }
    }
}