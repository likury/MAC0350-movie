package com.movies.backend.services.impl

import com.movies.backend.domain.entities.MovieEntity
import com.movies.backend.domain.dto.MovieDto
import com.movies.backend.repositories.MovieRepository
import com.movies.backend.services.MovieService
import com.movies.backend.services.TmdbService
import com.movies.backend.mappers.toDto
import com.movies.backend.mappers.toEntity
import org.springframework.stereotype.Service

@Service
class MovieServiceImpl(
    private val movieRepository: MovieRepository,
    private val tmdbService: TmdbService
) : MovieService {

    override fun save(movieEntity: MovieEntity): MovieEntity {
        return movieRepository.save(movieEntity)
    }

    override fun findByTmdbId(tmdbId: Long): MovieEntity? {
        return movieRepository.findByTmdbId(tmdbId)
    }

    override fun getMovieDetails(tmdbId: Long): MovieDto {
        // First check if we have the movie in our database
        val existingMovie = findByTmdbId(tmdbId)
        if (existingMovie != null) {
            return existingMovie.toDto()
        }

        // If not, fetch from TMDB and save to our database
        val movieDto = tmdbService.getMovieDetails(tmdbId)
        val savedMovie = save(movieDto.toEntity())
        return savedMovie.toDto()
    }

    override fun searchMovies(query: String, page: Int): List<MovieDto> {
        return tmdbService.searchMovies(query, page)
    }

    override fun getPopularMovies(page: Int): List<MovieDto> {
        return tmdbService.getPopularMovies(page)
    }
}
