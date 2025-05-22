package com.movies.backend.mappers

import com.movies.backend.domain.dto.MovieDto
import com.movies.backend.domain.entities.MovieEntity

fun MovieEntity.toDto(): MovieDto = MovieDto(
    id = this.id,
    tmdbId = this.tmdbId,
    title = this.title,
    posterPath = this.posterPath
)

fun MovieDto.toEntity(): MovieEntity = MovieEntity(
    id = this.id,
    tmdbId = this.tmdbId,
    title = this.title,
    posterPath = this.posterPath
)
