package com.movies.backend.domain.dto

data class SubmitLikeRequest(
    val userId: Long,
    val reviewId: Long
)
