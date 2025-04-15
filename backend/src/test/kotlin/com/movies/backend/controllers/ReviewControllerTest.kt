package com.movies.backend.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.movies.backend.config.TestSecurityConfig
import com.movies.backend.domain.dto.SubmitReviewRequest
import com.movies.backend.domain.entities.MovieEntity
import com.movies.backend.domain.entities.ReviewEntity
import com.movies.backend.domain.entities.UserEntity
import com.movies.backend.services.MovieService
import com.movies.backend.services.ReviewService
import com.movies.backend.services.UserService
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
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime
import java.util.*

@WebMvcTest(ReviewController::class)
@Import(TestSecurityConfig::class, ReviewControllerTest.Config::class)
class ReviewControllerTest {

    @TestConfiguration
    class Config {
        @Bean fun reviewService(): ReviewService = mock()
        @Bean fun userService(): UserService = mock()
        @Bean fun movieService(): MovieService = mock()
    }

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper
    @Autowired lateinit var reviewService: ReviewService
    @Autowired lateinit var userService: UserService
    @Autowired lateinit var movieService: MovieService

    @Test
    fun `should submit a review`() {
        val user = UserEntity(id = 1, username = "lucas", email = "lucas@email.com", passwordHash = "123")
        val movie = MovieEntity(id = 1, tmdbId = 100, title = "Inception", posterPath = "/inception.jpg")
        val request = SubmitReviewRequest(content = "Great!", rating = 5)
        val savedReview = ReviewEntity(
            id = 10,
            user = user,
            movie = movie,
            content = request.content,
            rating = request.rating,
            createdAt = LocalDateTime.now()
        )

        whenever(userService.findById(user.id!!)).thenReturn(Optional.of(user))
        whenever(movieService.findByTmdbId(movie.tmdbId)).thenReturn(movie)
        whenever(reviewService.save(any())).thenReturn(savedReview)

        mockMvc.post("/api/reviews/user/${user.id}/movie/${movie.tmdbId}") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            with(csrf())
        }.andExpect {
            status { isOk() }
        }
    }
}
