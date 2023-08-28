package com.example.rating.adapter.repository.config

import com.example.rating.adapter.extensions.toMap
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database
import java.util.*

fun configureDatabase(config: ApplicationConfig): Database {
    val dbProps = getDbProperties(config)
    return Database.connect(
        url = dbProps.getProperty("url"),
        user = dbProps.getProperty("user"),
        driver = dbProps.getProperty("driver"),
        password = dbProps.getProperty("password")
    )
}

private fun getDbProperties(config: ApplicationConfig) =
    Properties().apply {
        putAll(config.toMap("exposed.postgres"))
    }
