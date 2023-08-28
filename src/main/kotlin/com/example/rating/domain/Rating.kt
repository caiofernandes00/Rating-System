package com.example.rating.domain

import kotlinx.serialization.Serializable

@Serializable
data class Rating(val movieId: Long = 1L, val rating: Double = 0.0)

@Serializable
data class RatingAverage(val movieId: Long = 1L, val average: Double = 0.0)

@Serializable
data class CountAndSum(var count: Long = 0L, var sum: Double = 0.0)