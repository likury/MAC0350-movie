package com.movies.backend.services

import com.movies.backend.domain.dto.MovieDto

interface TmdbService {
    fun getMovieDetails(tmdbId: Long): MovieDto
    fun searchMovies(query: String, page: Int = 1): List<MovieDto>
    fun getPopularMovies(page: Int = 1): List<MovieDto>
} 