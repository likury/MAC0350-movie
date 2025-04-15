package com.movies.backend.domain.dto

data class MovieDto(
    val id: Long?,
    val tmdbId: Long,
    val title: String,
    val posterPath: String?
)
