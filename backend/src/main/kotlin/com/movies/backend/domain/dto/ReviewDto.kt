package com.movies.backend.domain.dto

import java.time.LocalDateTime

data class ReviewDto(
    val id: Long?,
    val userId: Long,
    val movieId: Long,
    val tmdbMovieId: Long,
    val content: String,
    val rating: Int,
    val createdAt: LocalDateTime
)
