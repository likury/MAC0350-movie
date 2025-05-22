package com.movies.backend.mappers

import com.movies.backend.domain.dto.ReviewDto
import com.movies.backend.domain.entities.ReviewEntity

fun ReviewEntity.toDto(): ReviewDto {
    return ReviewDto(
        id = this.id,
        userId = this.user.id!!,
        movieId = this.movie.id!!,
        tmdbMovieId = this.movie.tmdbId,
        content = this.content,
        rating = this.rating,
        createdAt = this.createdAt
    )
}
