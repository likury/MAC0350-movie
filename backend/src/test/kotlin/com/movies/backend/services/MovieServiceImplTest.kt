package com.movies.backend.services

import com.movies.backend.domain.entities.MovieEntity
import com.movies.backend.repositories.MovieRepository
import com.movies.backend.services.impl.MovieServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class MovieServiceImplTest {

    private val movieRepository: MovieRepository = mock()
    private val movieService = MovieServiceImpl(movieRepository)

    @Test
    fun `should save a movie`() {
        val movie = MovieEntity(tmdbId = 123, title = "Inception", posterPath = "/poster.jpg")
        whenever(movieRepository.save(movie)).thenReturn(movie.copy(id = 1))

        val result = movieService.save(movie)

        assertEquals("Inception", result.title)
        verify(movieRepository).save(movie)
    }

    @Test
    fun `should find movie by TMDB id`() {
        val movie = MovieEntity(id = 1, tmdbId = 123, title = "Inception", posterPath = null)
        whenever(movieRepository.findByTmdbId(123)).thenReturn(movie)

        val result = movieService.findByTmdbId(123)

        assertEquals("Inception", result?.title)
    }
}
