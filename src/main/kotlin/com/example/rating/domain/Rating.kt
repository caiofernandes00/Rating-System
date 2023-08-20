package com.example.rating.domain

import kotlinx.serialization.Serializable

@Serializable
data class Rating(val movieId: Long = 1L, val rating: Double = 0.0)
