package com.example.rating.adapter.repository

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

abstract class BaseRepository(private val database: Database) {
    abstract val table: Table

    init {
        transaction {
            SchemaUtils.create(table)
        }
    }
}