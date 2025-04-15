package com.movies.backend.services

import com.movies.backend.domain.entities.MovieEntity

interface MovieService {
    fun save(movieEntity: MovieEntity): MovieEntity
    fun findByTmdbId(tmdbId: Long): MovieEntity?
}
