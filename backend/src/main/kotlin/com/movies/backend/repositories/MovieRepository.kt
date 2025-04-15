package com.movies.backend.repositories

import com.movies.backend.domain.entities.MovieEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MovieRepository : JpaRepository<MovieEntity, Long> {
    fun findByTmdbId(tmdbId: Long): MovieEntity?
}
