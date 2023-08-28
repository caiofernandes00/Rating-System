package com.example.rating.adapter.repository

import com.example.rating.domain.Rating
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate
import java.util.*

class RatingsRepository(private val database: Database) : BaseRepository(database) {
    object Ratings : Table() {
        val id = varchar("id", 36)
        val movieId = ulong("movie_id")
        val rating = double("rating")
        val createdAt = date("created_at").default(LocalDate.now())
        val updatedAt = date("updated_at").nullable()

        override val primaryKey = PrimaryKey(id, name = "PK_Rating_ID")
    }

    override val table: Table
        get() = Ratings

    suspend fun create(ratingDomain: Rating): String = query {
        Ratings.insert {
            it[id] = UUID.randomUUID().toString()
            it[movieId] = ratingDomain.movieId.toULong()
            it[rating] = ratingDomain.rating
        }[Ratings.id]
    }
}