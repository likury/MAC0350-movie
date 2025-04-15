package com.movies.backend.services.impl

import com.movies.backend.domain.entities.MovieEntity
import com.movies.backend.repositories.MovieRepository
import com.movies.backend.services.MovieService
import org.springframework.stereotype.Service

@Service
class MovieServiceImpl(
    private val movieRepository: MovieRepository
) : MovieService {

    override fun save(movieEntity: MovieEntity): MovieEntity {
        return movieRepository.save(movieEntity)
    }

    override fun findByTmdbId(tmdbId: Long): MovieEntity? {
        return movieRepository.findByTmdbId(tmdbId)
    }
}
