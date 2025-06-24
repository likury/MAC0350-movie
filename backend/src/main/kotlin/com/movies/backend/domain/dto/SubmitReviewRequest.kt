package com.movies.backend.domain.dto

data class SubmitReviewRequest(
    val content: String,
    val rating: Int
)
