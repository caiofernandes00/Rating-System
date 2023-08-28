package com.example.rating.adapter.repository

import com.example.rating.domain.RatingAverage
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate
import java.util.*

class RatingsAverageRepository(private val database: Database) : BaseRepository(database) {
    object RatingsAverage : Table() {
        val id = varchar("id", 36)
        val movieId = ulong("movie_id")
        val average = double("average")
        val createdAt = date("created_at").default(LocalDate.now())
        val updatedAt = date("updated_at").nullable()

        override val primaryKey = PrimaryKey(id, name = "PK_Ratings_Average_ID")
    }

    override val table: Table
        get() = RatingsAverage

    suspend fun create(ratingDomain: RatingAverage): String = query {
        RatingsAverage.insert {
            it[id] = UUID.randomUUID().toString()
            it[movieId] = ratingDomain.movieId.toULong()
            it[average] = ratingDomain.average
        }[RatingsAverage.id]
    }
}