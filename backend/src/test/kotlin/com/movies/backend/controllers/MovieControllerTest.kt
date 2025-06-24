package com.movies.backend.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.movies.backend.config.TestSecurityConfig
import com.movies.backend.domain.entities.MovieEntity
import com.movies.backend.services.MovieService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(MovieController::class)
@Import(MovieControllerTest.Config::class, TestSecurityConfig::class)
class MovieControllerTest {

    @TestConfiguration
    class Config {
        @Bean fun movieService(): MovieService = mock()
    }

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper
    @Autowired lateinit var movieService: MovieService

    @Test
    fun `should add a new movie`() {
        val movie = MovieEntity(id = 1, tmdbId = 12345, title = "Inception", posterPath = "/inception.jpg")

        whenever(movieService.save(any())).thenReturn(movie)

        mockMvc.post("/api/movies") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(movie)
            with(csrf())
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `should return movie by tmdbId`() {
        val movie = MovieEntity(id = 1, tmdbId = 12345, title = "Inception", posterPath = "/inception.jpg")
        whenever(movieService.findByTmdbId(12345)).thenReturn(movie)

        mockMvc.get("/api/movies/tmdb/12345") {
            with(csrf())
        }.andExpect {
            status { isOk() }
        }
    }
}
